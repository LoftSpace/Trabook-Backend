package Trabook.PlanManager.controller;

import Trabook.PlanManager.response.PlanListResponseDTO;
import Trabook.PlanManager.service.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/plan/admin")
public class AdminController {



    private ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;
     private final PlanService planService;

    @Autowired
    public AdminController(RedisTemplate<String, String> redisTemplate,
                           PlanService planService) {
        this.redisTemplate = redisTemplate;
        this.planService = planService;
    }




    //@Scheduled(cron = "0 * * * * *")
    //@Scheduled(cron = "0 0 0/1 * * *")
    @ResponseBody
    @GetMapping("/updateHottestPlan")
    public void updateHottestPlan()  {
        List<PlanListResponseDTO> TopPlans = planService.getHottestPlan(0L);
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
        HashOperations<String,String,String> hashops = redisTemplate.opsForHash();
        objectMapper.registerModule(new JavaTimeModule());

        for(PlanListResponseDTO plan : TopPlans){
            try {
                String planString = objectMapper.writeValueAsString(plan);
                String planKey = "plan:content:" + plan.getPlanId();
                hashops.put("plans",planKey,planString);

                int likes = plan.getLikes();
                Boolean topPlans = zsetOps.add("topPlans", planKey, likes);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }






/*
    //상위 5개 여행지 가져오고 레디스 서버에 업데이트
    //@Scheduled(cron = "0 0 0/1 * * * ")
    //@ResponseBody
    //@GetMapping("/updateHottestPlace")
    @Scheduled(cron = "0 * * * * *")
    public void updateHottestPlace()  {

        List<PlaceForModalDTO> TopPlaces = destinationService.getHottestPlace(0L); //상위 10개 순위 데이터
        ZSetOperations<String,String> zsetOps = redisTemplate.opsForZSet();
       for(PlaceForModalDTO place : TopPlaces){
            System.out.println("insert"+place.getPlace().getPlaceName()+ " and score is "+ place.getPlace().getRatingScore() );
            try {
                String placeString = objectMapper.writeValueAsString(place.getPlace());
                Double score = place.getPlace().getRatingScore();
                Boolean add = zsetOps.add("topPlaces", placeString, score);
                System.out.println(add);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


 */



}
