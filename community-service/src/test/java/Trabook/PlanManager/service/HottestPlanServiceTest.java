package Trabook.PlanManager.service;


import Trabook.PlanManager.Scheduler.UpdateHottestPlanScheduler;
import Trabook.PlanManager.repository.plan.PlanListRepository;
import Trabook.PlanManager.response.PlanListResponseDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class HottestPlanServiceTest{
    @Autowired
    HottestPlanService hottestPlanService;
    @Autowired
    PlanListRepository planListRepository;
    @Autowired
    UpdateHottestPlanScheduler updateHottestPlanScheduler;
    @Autowired
    PlanRedisService planRedisService;

    List<PlanListResponseDTO> hottestPlan;

    @BeforeEach
    void initHottestPlanList() {
        hottestPlan = planListRepository.findHottestPlan();
        hottestPlanService.updateHottestPlanIds();
    }

    @Test
    void checkHottestPlanListTest(){

        ArrayList<Long> hottestPlanIds = hottestPlanService.getHottestPlanIds();
        for(PlanListResponseDTO planListResponseDTO : hottestPlan){
            Assertions.assertThat(hottestPlanIds.contains(planListResponseDTO.getPlanId()));
        }
    }

    @Test
    void isHottestPlanTest(){
        long normalPlanId = 0;
        Assertions.assertThat(hottestPlanService.isHottestPlan(normalPlanId)).isFalse();
    }

    @Test
    void updateHottestPlanToRedis(){
        updateHottestPlanScheduler.updateHottestPlanToRedis();
        List<PlanListResponseDTO> hottestPlanFromRedis = planRedisService.getHottestPlan();
        List<PlanListResponseDTO> hottestPlanFromDB = planListRepository.findHottestPlan();

        for(int i = 0; i < hottestPlanFromRedis.size(); i++){
            Assertions.assertThat(hottestPlanFromRedis.get(i).equals(hottestPlanFromDB.get(i))).isTrue();
        }
    }
}
