package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.dto.CommentRequestDTO;
import Trabook.PlanManager.domain.destination.Place;
import Trabook.PlanManager.domain.plan.*;
import Trabook.PlanManager.dto.PlanCreateDTO;
import Trabook.PlanManager.dto.PlanGeneralDTO;
import Trabook.PlanManager.repository.destination.DestinationRepository;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.response.PlanResponseDTO;
import Trabook.PlanManager.service.destination.PointDeserializer;
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

    @Transactional
    public long createPlan(PlanCreateDTO planCreateDTO) {
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

    @Transactional
    public long addComment(CommentRequestDTO comment) {
        long planId = comment.getPlanId();
        planRepository.findById(planId)
                 .orElseThrow(() -> new IllegalArgumentException(String.format("plan not found")));
        planRepository.increaseCommentCount(planId);
        return planRepository.addComment(comment);
    }

    @Transactional
    public Comment getComment(long commentId) {
        Comment comment = planRepository.findCommentById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("comment not found")));
        return comment;

    }

    @Transactional
    public PlanResponseDTO getPlan(long planId, Long userId) {

        PlanResponseDTO result = planRepository.findTotalPlan(planId);
        Plan plan = result.getPlan();

        List<DayPlan> dayPlanList = plan.getDayPlanList();

        List<Place> placeList = destinationRepository.findPlaceListByPlanId(plan.getPlanId());
        int placeIndex = 0;

        for(DayPlan dayPlan : dayPlanList) {
            for(DayPlan.Schedule schedule : dayPlan.getScheduleList()) {
                Place place = placeList.get(placeIndex);
                schedule.setImageSrc(place.getImgSrc());
                schedule.setLongitude(place.getLongitude());
                schedule.setLatitude(place.getLatitude());
                schedule.setPlaceName(place.getPlaceName());
                schedule.setSubcategory(place.getSubcategory());
                schedule.setAddress(place.getAddress());
                placeIndex ++;
            }

        }

        boolean isLiked;
        boolean isScrapped;
        if(userId == null){
            isLiked = false;
            isScrapped = false;
        } else {
            isLiked = planRepository.isLiked(planId, userId);
            isScrapped = planRepository.isScrapped(planId, userId);
        }
        result.setLiked(isLiked);
        result.setScrapped(isScrapped);
        result.setTags(getTags(placeList));
        List<Comment> comments = planRepository.findCommentListByPlanId(planId);
        result.setComments(comments);
        return result;


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
    public String deleteComment(long commentId) {

        Comment comment = planRepository.findCommentById(commentId).get();
        planRepository.findCommentById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("comment already deleted")));

        // 대댓글인지 확인
        if(comment.getRefOrder()==0) {
            planRepository.deleteCommentByRef(comment.getParentId(),commentId, comment.getPlanId());
            return "delete complete";
        } else {
            planRepository.deleteComment(commentId);
            return "delete complete";
        }

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
    public List<PlanGeneralDTO> findCustomPlanList(String search,
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
