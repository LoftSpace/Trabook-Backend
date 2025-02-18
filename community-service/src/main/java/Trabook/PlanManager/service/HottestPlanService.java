package Trabook.PlanManager.service;

import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class HottestPlanService {
    private final PlanListRepository planListRepository;
    private final Set<Long> hottestPlanIds = new HashSet<>();
    private final RedissonClient redissonClient;
    private final RedisTemplate<String,Long> longRedisTemplate;

    @Scheduled(cron = "0 * * * * *")
    public void updateHottestPlanIds() {
        List<PlanListResponseDTO> hottestPlan = planListRepository.findHottestPlan();
        for(PlanListResponseDTO planListResponseDTO : hottestPlan){
            hottestPlanIds.add(planListResponseDTO.getPlanId());
        }
        log.info("{} complete","인기게시글 업데이트 완료");

    }
    public void likePlan(long planId,long userId){
        RSet<String> userPlanLikeSet = redissonClient.getSet("plan:likes-user:" + planId);
        if(userPlanLikeSet.add(Long.toString(userId)))
            longRedisTemplate.opsForHash().increment("plan:likes", Long.toString(planId), 1);

    }
    // 인기 게시글 목록 가져오기
    public Set<Long> getHottestPlanIds() {
        return hottestPlanIds;
    }

    public boolean isHottestPlan(Long planId){
        return hottestPlanIds.contains(planId);
    }
}
