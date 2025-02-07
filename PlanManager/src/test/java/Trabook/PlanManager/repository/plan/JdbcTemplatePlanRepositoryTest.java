package Trabook.PlanManager.repository.plan;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.dto.PlanCreateDto;
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
class JdbcTemplatePlanRepositoryTest {


    @Autowired
    private PlanRepository planRepository;

    @Test
    void create() {
        PlanCreateDto planCreateDTO = new PlanCreateDto(3,"경기도", LocalDate.parse("2024-09-01"),LocalDate.parse("2024-09-08"));
        long plan = planRepository.createPlan(planCreateDTO);
        Optional<Plan> result = planRepository.findById(plan);
        Plan resultPlan = result.get();

        Assertions.assertThat(resultPlan.getPlanId()).isEqualTo(plan);
    }

    @Test
    void findById() {
    }
}