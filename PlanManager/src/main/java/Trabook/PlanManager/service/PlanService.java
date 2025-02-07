package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.user.User;
import Trabook.PlanManager.dto.CommentRequestDto;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.dto.CommentWithUser;
import Trabook.PlanManager.dto.PlanCreateDto;
import Trabook.PlanManager.dto.PlanGeneralDto;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.response.PlanResponseDTO;
import Trabook.PlanManager.service.destination.PointDeserializer;
import Trabook.PlanManager.service.webclient.WebClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final PlanListRepository planListRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClientService webClientService;
    @Transactional
    public long createPlan(PlanCreateDto planCreateDTO) {
        return planRepository.createPlan(planCreateDTO);
    }

    @Transactional
    public long updatePlan(Plan plan) {
        long planId = plan.getPlanId();

        if(planRepository.findById(planId).isEmpty()) {
            return 0;
        }

        if(isPlanExistInRanking(planId)) {
            System.out.println("plan exists in ranking");
            //write through
            modifyPlanInRanking(plan);
        }

        planId = updatePlanToDB(plan);
        return planId;
    }

    private long updatePlanToDB(Plan plan) {
        long planId;
        plan.setImgSrc("https://storage.googleapis.com/trabook-20240822/planPhoto/" + Long.toString(plan.getPlanId()));


        List<DayPlan> dayPlanList = plan.getDayPlanList();
        planId = planRepository.updatePlan(plan);

        List<DayPlan> oldDayPlanList = planRepository.findDayPlanListByPlanId(planId);
        int updatePlanDays = plan.getDayPlanList().size();
        int oldPlanDays = oldDayPlanList.size();

        if (updatePlanDays < oldPlanDays) {
            for(int i = updatePlanDays; i < oldPlanDays; i++) {
                long planIdOfOldPlan = oldDayPlanList.get(i).getPlanId();
                long dayOfOldPlan = oldDayPlanList.get(i).getDay();
                planRepository.deleteDayPlanById(planIdOfOldPlan, dayOfOldPlan);
                planRepository.deleteScheduleById(planIdOfOldPlan, dayOfOldPlan);
            }
        }


        if(dayPlanList != null) {
            for (DayPlan dayPlan : dayPlanList) {
                dayPlan.setPlanId(planId);
                updateOrSaveDayPlan(dayPlan);
                updateOrSaveSchedule(plan, dayPlan);
            }
        }
        return planId;
    }

    private boolean isPlanExistInRanking(long planId) {

        List<PlanListResponseDTO> hottestPlans = getHottestPlans();

        for(PlanListResponseDTO planListResponseDTO : hottestPlans) {
            if(planListResponseDTO.getPlanId() == planId) {
                return true;
            }
        }
        return false;
    }

    private void modifyPlanInRanking(Plan plan)  {
        HashOperations<String,String,String> hashops = redisTemplate.opsForHash();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String planString = objectMapper.writeValueAsString(plan);
            String planKey = "plan:content:" + plan.getPlanId();
            hashops.put("plans", planKey, planString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<PlanListResponseDTO> getHottestPlans() {
        objectMapper.registerModule(new SimpleModule().addDeserializer(Point.class, new PointDeserializer()));
        objectMapper.registerModule(new JavaTimeModule());
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        List<String> topPlans = hashOps.values("plans");

        List<PlanListResponseDTO> top10Plans = new ArrayList<>();

        try {
            for (String jsonPlan : topPlans) {
                PlanListResponseDTO plan = objectMapper.readValue(jsonPlan, PlanListResponseDTO.class);
                top10Plans.add(plan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top10Plans;
    }


    private void updateOrSaveSchedule(Plan plan, DayPlan dayPlan) {
        int day = dayPlan.getDay();
        long planId = plan.getPlanId();
        for (DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
            int order = schedule.getOrder();
            long placeId = schedule.getPlaceId();

            schedule.setPlanId(planId);
            schedule.setDay(day);

            destinationRepository.updateNumOfAdded(placeId);

            if(planRepository.findSchedule(planId,day,order).isPresent()) // 새로 생성한 스케쥴
                planRepository.updateSchedule(schedule);
            else // 기존에 있는 스케쥴 업데이트
                planRepository.saveSchedule( schedule);

            placeRatingScoreUp(plan, schedule);
        }
    }

    private void placeRatingScoreUp(Plan plan, DayPlan.Schedule schedule) {
        if (plan.isFinished()) // 점수 추가
            destinationRepository.scoreUp(schedule.getPlaceId());
        // 레디스에 있는 목록인지 확인 로직 추가
    }

    private void updateOrSaveDayPlan(DayPlan dayPlan) {
        long planId = dayPlan.getPlanId();
        int day = dayPlan.getDay();
        if(planRepository.findDayPlan(planId,day).isPresent()) { // 새로 생성한 dayplan
            planRepository.updateDayPlan(dayPlan);
        } else { // 기존에 있는 dayplan
            planRepository.saveDayPlan(dayPlan);
        }
    }
    //TODO:요청 dto 처리 서비스레이어 추후에 추가
    public long handleAddComment(CommentRequestDto commentRequestDto){
        Comment comment = commentRequestDto.toEntity();
        return addComment(comment);
    }

    @Transactional
    public long addComment(Comment comment) {
        long planId = comment.getPlanId();
        planRepository.findById(planId)
                 .orElseThrow(() -> new IllegalArgumentException(String.format("Plan 찾을 수 없음")));
        planRepository.increaseCommentCount(planId);
        return planRepository.addComment(comment);
    }

    @Transactional
    public Comment getComment(long commentId) {
        Comment comment = planRepository.findCommentById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("comment not found")));
        return comment;

    }
    public PlanResponseDTO handleTotalPlanRequest(long planId,Long userId){
        PlanResponseDTO totalPlan = planRepository.findTotalPlan(planId);
        Plan plan = totalPlan.getPlan();
        List<Place> placeList = destinationRepository.findPlaceListByPlanId(planId);
        // get vs set ???
        setPlanOwner(plan.getUserId(),totalPlan);
        setCommentsWithUsers(planId, totalPlan);
        setDetailPlaceInfo(plan,placeList);

        totalPlan.isLiked(isPlanLiked(planId, userId));
        totalPlan.isScrapped(isPlanScrapped(planId,userId));
        totalPlan.setTags(getTags(placeList));
        return totalPlan;
    }

    private boolean isPlanLiked(long planId, Long userId) {
        if(userId == null)
            return false;
        else
            return planRepository.isLiked(planId, userId);
    }
    private boolean isPlanScrapped(long planId, Long userId){
        if(userId == null)
            return false;
        else
            return planRepository.isScrapped(planId,userId);
    }

    private void setCommentsWithUsers(long planId, PlanResponseDTO totalPlan) {
        
        List<Comment> commentList = planRepository.findCommentListByPlanId(planId);
        //각 댓글의 유저데이터 가져오기
        List<Long> commentUserIdList = getCommentUserList(commentList);
        List<User> userList = webClientService.getUserInfoList(commentUserIdList);
        List<CommentWithUser> commentWithUserList = createCommentWithUserList(userList, commentList);
        totalPlan.setComments(commentWithUserList);

    }

    private static List<CommentWithUser> createCommentWithUserList(List<User> users, List<Comment> commentList) {
        List<CommentWithUser> commentWithUserList = new ArrayList<>();
        for(int indexOfUserList = 0; indexOfUserList < users.size(); indexOfUserList++){
            Comment comment = commentList.get(indexOfUserList);
            User user = users.get(indexOfUserList);
            commentWithUserList.add(new CommentWithUser(comment,user));
        }
        return commentWithUserList;
    }

    private static List<Long> getCommentUserList(List<Comment> commentList) {
        List<Long> commentUserIds = new ArrayList<>();
        for(Comment comment : commentList) {
            long commentUserId = comment.getUserId();
            commentUserIds.add(commentUserId);
        }
        return commentUserIds;
    }

    private void setPlanOwner(long planOwnerId, PlanResponseDTO totalPlan) {
        User planOwner = webClientService.getUserInfo(planOwnerId);
        totalPlan.setPlanOwner(planOwner);
    }

    private void setDetailPlaceInfo(Plan plan,List<Place> placeList) {
        List<DayPlan> dayPlanList = plan.getDayPlanList();
        int placeIndex = 0;
        for(DayPlan dayPlan : dayPlanList) {
            for(DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
                Place place = placeList.get(placeIndex);
                schedule.addDetails(place);
                placeIndex ++;
            }
        }
    }



    public List<String> getTags(List<Place> placeList) {
        Set<String> tags = new HashSet<>();
        for(Place place : placeList) {
            tags.add(place.getSubcategory());
            if(tags.size() == 3)
                return new ArrayList<>(tags);
        }
        return new ArrayList<>(tags);
    }
    @Transactional
    public String deletePlan(long planId) {
        if(planRepository.deletePlan(planId) == 1)
            return "delete complete";
        else {
            return "error";
        }
    }

    @Transactional
    public String deleteLike(long userId,long planId){
        planRepository.deleteLike(userId,planId);
        planRepository.downLike(planId);
        return "delete complete";
    }

    @Transactional
    public String deleteScrap(long userId, long planId) {
        if(planRepository.deleteScrap(userId,planId) == 1) {
            int updatedScraps = planRepository.downScrap(planId);
            log.info("planId : {} scrap 수 감소[{} -> {}]",planId,updatedScraps-1,updatedScraps);
            return "delete complete";
        }else
            return "error";
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = planRepository.findCommentById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("댓글 찾을 수 없음")));
        if(comment.isRootComment())
            planRepository.deleteCommentByRef(comment.getParentId(),commentId, comment.getPlanId());
        else
            planRepository.deleteComment(commentId);
    }

    @Transactional
    public String likePlan(long planId, long userId) {

        if(isPlanExistInRanking(planId)){
            ZSetOperations<String, String> zsetOps = redisTemplate.opsForZSet();
            String planKey = "plan:content:" + planId;
            zsetOps.incrementScore("topPlans",planKey,1);
            return "like complete";
        } else {
            planRepository.findById(planId)
                    .orElseThrow(()-> new IllegalArgumentException(String.format("plan not found")));
            planRepository.likePlan(userId,planId);
            planRepository.upLike(planId);
            return "like complete";
        }

    }

    @Transactional
    public String scrapPlan(long planId, long userId) {
        planRepository.findById(planId)
                .orElseThrow(()-> new IllegalArgumentException(String.format("no plan exists")));
        planRepository.scrapPlan(userId,planId);
        planRepository.upScrap(planId);
        return "scrap complete";
    }

    @Transactional
    public List<PlanListResponseDTO> getHottestPlan(Long userId) {

        List<PlanListResponseDTO> top10Plans = planListRepository.findHottestPlan();
        return top10Plans;
    }

    @Transactional
    public List<PlanGeneralDto> findCustomPlanList(String search,
                                                   List<String> state,
                                                   Integer numOfPeople,
                                                   Integer duration,
                                                   String sorts,
                                                   Integer userId,
                                                   Boolean userScrapOnly) {
        return planListRepository.findCustomPlanList(search, state, numOfPeople, duration, sorts, userId, userScrapOnly);
    }

    // null인지 아닌지 확인해서 boolean으로 반환하는 방식으로 바꾸기
    public Optional<Plan> validateDuplicatePlanName(Plan plan){

        return planRepository.findPlanByUserAndName(plan.getUserId(),plan.getTitle());

    }
}
