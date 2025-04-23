package Trabook.PlanManager.configuration;

import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.service.PlanRedisService;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.MapOptions;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.MapWriter;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ReddissonConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    private final PlanRepository planRepository;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }

    @Bean
    public RMap<String,Long> userPlanLikeMap() {
        MapWriter<String, Long> userPlanLikeMapWriter = userPlanLikeMapWriter();
        MapOptions options = MapOptions.<String, Long> defaults()
                .writer(userPlanLikeMapWriter)
                .writeMode(MapOptions.WriteMode.WRITE_BEHIND)
                .writeBehindDelay(3);
        return redissonClient().getMap("user:plan:likes",options);
    }

    private MapWriter<String, Long> userPlanLikeMapWriter() {
        return new MapWriter<String, Long>() {
            @Override
            public void write(Map<String, Long> map) {
                for (Map.Entry<String, Long> e : map.entrySet()) {
                    String compositeKey = e.getKey();      // e.g. "42:100"
                    String[] parts = compositeKey.split(":");
                    Long userId = Long.valueOf(parts[0]);
                    Long planId = Long.valueOf(parts[1]);
                    planRepository.likePlan(userId, planId);
                }
            }

            @Override
            public void delete(Collection<String> collection) {

            }


        };

    }
}

