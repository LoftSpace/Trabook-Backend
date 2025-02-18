package Trabook.PlanManager.service;

import Trabook.PlanManager.domain.comment.Comment;
import Trabook.PlanManager.domain.plan.TotalPlan;
import Trabook.PlanManager.repository.plan.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Transactional
public class PlanServiceTest {

    @Autowired
    @Qualifier("redisTemplateForLong")
    RedisTemplate<String, Integer> longRedisTemplate;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    PlanService planService;

    @Autowired
    HottestPlanService hottestPlanService;
    private ExecutorService executorService;
    private CountDownLatch latch;
    private static final int TOTAL_COUNT = 100;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(TOTAL_COUNT);
        latch = new CountDownLatch(TOTAL_COUNT);
        hottestPlanService.updateHottestPlanIds();
    }

    @Test
    void hottestPlanLikeParallelTest() throws InterruptedException {
        HashOperations<String, String,Integer> hashOps = longRedisTemplate.opsForHash();
        Integer before = hashOps.get("plan:likes", Long.toString(530));

        for (int i = 0; i < TOTAL_COUNT; i++){
            int userId = i + 1;
            executorService.submit(() ->  {
                try {
                    planService.likePlan(530,userId);
                }
                catch( Exception e){
                    System.out.println(e);
                } finally {
                    latch.countDown();

                }
            });
        }
        latch.await();

        TotalPlan plan = planRepository.findById(530).get();
        hashOps = longRedisTemplate.opsForHash();
        Integer after = hashOps.get("plan:likes", Long.toString(530));

        Assertions.assertThat(plan.getLikes()).isNotEqualTo(after);
        Assertions.assertThat(after).isEqualTo(before + TOTAL_COUNT);

    }
    @Test
    void planLikeParallelTest() throws InterruptedException {

        TotalPlan plan = planRepository.findById(50).get();
        int before = plan.getLikes();

        for (int i = 0; i < TOTAL_COUNT; i++){
            int userId = i + 1;
            executorService.submit(() ->  {
                try {
                    planService.likePlan(50,userId);
                }
                catch( Exception e){
                    System.out.println(e);
                } finally {
                    latch.countDown();

                }
            });
        }
        latch.await();

        plan = planRepository.findById(50).get();
        int after = plan.getLikes();

        Assertions.assertThat(after).isEqualTo(before + TOTAL_COUNT);

    }
}