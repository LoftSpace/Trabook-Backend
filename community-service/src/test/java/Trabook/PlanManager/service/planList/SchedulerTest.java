package Trabook.PlanManager.service.planList;

import Trabook.PlanManager.Scheduler.WriteBackService;
import Trabook.PlanManager.domain.plan.TotalPlan;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.HottestPlanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SchedulerTest {
    @Autowired
    WriteBackService writeBackService;
    @Autowired
    HottestPlanService hottestPlanService;
    @Autowired
    PlanRepository planRepository;


    @BeforeEach
    void setUp(){
        hottestPlanService.updateHottestPlanIds();
        hottestPlanService.likePlan(580,3);
        hottestPlanService.likePlan(580,4);
        hottestPlanService.likePlan(530,3);
    }

    @Test
    void writeBackLikesCountingTest(){
        TotalPlan plan1 = planRepository.findById(580).get();
        TotalPlan plan2 = planRepository.findById(530).get();
        int likeOfPlan1Before = plan1.getLikes();
        int likeOfPlan2Before = plan2.getLikes();

        writeBackService.writeBackLikeCounting();

        plan1 = planRepository.findById(580).get();
        plan2 = planRepository.findById(530).get();
        int likeOfPlan1After = plan1.getLikes();
        int likeOfPlan2After = plan2.getLikes();

        Assertions.assertThat(likeOfPlan1After).isEqualTo(likeOfPlan1Before + 2);
        Assertions.assertThat(likeOfPlan2After).isEqualTo(likeOfPlan2Before + 1);
    }

    @Test
    void writeBackLikesTest(){
        writeBackService.writeBackUserLike();
        Assertions.assertThat(planRepository.isLiked(580,3)).isTrue();
        Assertions.assertThat(planRepository.isLiked(580,4)).isTrue();
        Assertions.assertThat(planRepository.isLiked(530,3)).isTrue();
    }
}
