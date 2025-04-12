package Trabook.PlanManager.service;

import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HottestPlanService {
    private final PlanListRepository planListRepository;
    private final ArrayList<Long> hottestPlanIds = new ArrayList<>();
    private final RedissonClient redissonClient;
    private final RedisTemplate<String,Long> longRedisTemplate;

    @Scheduled(cron = "0 * * * * *")
    public void updateHottestPlanIdsToLocal() {
        List<PlanListResponseDTO> hottestPlan = planListRepository.findHottestPlan();
        for(PlanListResponseDTO planListResponseDTO : hottestPlan){
            hottestPlanIds.add(planListResponseDTO.getPlanId());
        }

        log.info("{} complete","인기게시글 업데이트 완료");

    }

    public void likePlan(long planId,long userId){
        if(redissonClient.getSet("plan:likes-user:" + planId).add(Long.toString(userId)))
            redissonClient.getMap("plan:likes").addAndGet(planId,1);
    }

    // 인기 게시글 목록 가져오기
    public ArrayList<Long> getHottestPlanIds()
    {
        return hottestPlanIds;
    }

    public boolean isHottestPlan(Long planId){
        return hottestPlanIds.contains(planId);
    }

    public Map<String,Integer> getPlanHottestLikesCountingMap() {
        HashOperations<String, String,Integer> hashOps = longRedisTemplate.opsForHash();
        return hashOps.entries("plan:likes");
    }
}
