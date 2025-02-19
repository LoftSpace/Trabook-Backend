package Trabook.PlanManager.Scheduler;


import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.HottestPlanService;
import Trabook.PlanManager.service.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;



import java.util.List;


@Component
@RequiredArgsConstructor
public class UpdateHottestPlanScheduler {
    private ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;
    private final PlanService planService;
    private final HottestPlanService hottestPlanService;
    private final WriteBackService writeBackService;


    //@Scheduled()
    public void updateHottestPlan(){
        hottestPlanService.updateHottestPlanIds();
        updateHottestPlanToRedis();
        writeBackService.writeBackLikeCounting();
        writeBackService.writeBackLikeCounting();
    }

    public void updateHottestPlanToRedis() {
        List<PlanListResponseDTO> TopPlans = planService.getHottestPlan(0L);
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        HashOperations<String, String, String> hashops = redisTemplate.opsForHash();
        objectMapper.registerModule(new JavaTimeModule());

        redisTemplate.delete("topPlans");
        for (PlanListResponseDTO plan : TopPlans) {
            try {
                String planString = objectMapper.writeValueAsString(plan);
                listOps.rightPush("topPlans",planString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
