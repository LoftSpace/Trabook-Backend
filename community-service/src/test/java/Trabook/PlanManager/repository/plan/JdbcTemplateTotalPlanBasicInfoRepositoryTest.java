package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.PlanBasicInfo;
import Trabook.PlanManager.domain.plan.TotalPlan;
import Trabook.PlanManager.dto.PlanCreateRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
class JdbcTemplateTotalPlanBasicInfoRepositoryTest {


    @Autowired
    private PlanRepository planRepository;

    @Test
    void create() {
        PlanBasicInfo planBasicInfo = new PlanBasicInfo(3L,"경기도", LocalDate.parse("2024-09-01"),LocalDate.parse("2024-09-08"));
        long plan = planRepository.createPlan(planBasicInfo);
        Optional<TotalPlan> result = planRepository.findById(plan);
        TotalPlan resultTotalPlan = result.get();

        Assertions.assertThat(resultTotalPlan.getPlanId()).isEqualTo(plan);
    }

    @Test
    void findById() {
    }
}