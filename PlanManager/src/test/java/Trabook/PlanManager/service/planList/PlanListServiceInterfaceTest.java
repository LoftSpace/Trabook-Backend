package Trabook.PlanManager.service.planList;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

class PlanListServiceInterfaceTest {
/*
    @Autowired
    GetUserPlanList getUserPlanList;

    @Autowired
    GetUserScrapPlanList getUserScrapPlanList;

    @Autowired
    PlanService planService;


    @Test
    void getPlanList() {
        //given
        Plan plan1 = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "FIRST", "test");
        Plan plan2 = new Plan(-1, 3, 1, true, 0, 0, "2024-08-20 10:00:00", "SECOND", "test");
        long userId = 3;
        long userId2=4;
        long planId1 = planService.createPlan(plan1, null);
        long planId2 = planService.createPlan(plan2, null);

        plan1.setPlanId(planId1);
        plan2.setPlanId(planId2);

        //when
        List<Plan> planList = getUserPlanList.getPlanList(userId);
        List<Plan> planList2 = getUserPlanList.getPlanList(4);
        //then
        assertThat(planList).isNotEmpty();
        assertThat(planList.get(0)).usingRecursiveComparison().isEqualTo(plan1);
        assertThat(planList.get(1)).usingRecursiveComparison().isEqualTo(plan2);
        assertThat(planList2).isEmpty();
    }

 */
}