package Trabook.PlanManager.Scheduler;

import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.HottestPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WriteBackService {
    private final PlanRepository planRepository;
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedissonClient redissonClient;
    private final HottestPlanService hottestPlanService;

    //@Scheduled(fixedRate = 180000)
    public void writeBackLikeCounting() {
        Map<String,Integer> likesMap = hottestPlanService.getPlanHottestLikesCountingMap();
        updateLikes(likesMap);
    }

    //@Scheduled(fixedRate = 180000)
    public void writeBackUserLike() {

        ArrayList<Long> hottestPlanIds = hottestPlanService.getHottestPlanIds();
        //pipelining?
       
        for(Long hottestPlanId : hottestPlanIds){
            RSet<String> users = redissonClient.getSet("plan:likes-user:" + hottestPlanId);
            for(String userId : users){
                planRepository.likePlan(Long.parseLong(userId),hottestPlanId);
            }
        }
    }

    private void updateLikes(Map<String,Integer> likesMap) {
        if(likesMap != null && !likesMap.isEmpty()){
            for (Map.Entry<String,Integer> entry : likesMap.entrySet()) {
                Long planId = Long.parseLong(entry.getKey());
                Integer likesCount = entry.getValue();
                planRepository.updateLikes(planId, likesCount);
            }
        }
    }



}
