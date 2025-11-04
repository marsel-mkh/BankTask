package com.t1.marselmkh.metrics;

import com.t1.marselmkh.entity.ProductKey;
import com.t1.marselmkh.repository.ClientProductJdbcRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductsMetrics {

    private final ClientProductJdbcRepository jdbcRepository;
    private final RedisTemplate<String, Long> redisTemplateForMetrics;
    private final MeterRegistry registry;

    private static final String METRIC_KEY_PREFIX = "open_products_total:";

    @PostConstruct
    public void init() {
        for (ProductKey key : ProductKey.values()) {
            Gauge.builder("open_products", () ->
                            Optional.ofNullable(redisTemplateForMetrics.opsForValue().get(METRIC_KEY_PREFIX + key.name()))
                                    .map(Long::doubleValue)
                                    .orElse(0.0)
                    )
                    .description("Количество открытых продуктов по типам")
                    .tag("type", key.name())
                    .register(registry);
        }

        refreshMetrics();
    }

    @Scheduled(fixedRateString = "${metric.scheduler}")
    public void refreshMetrics() {
        try {
            log.info("Получение количества продуктов");
            Map<ProductKey, Long> counts = jdbcRepository.countOpenedByType();

            counts.forEach((key, count) ->
                redisTemplateForMetrics.opsForValue().set(METRIC_KEY_PREFIX + key.name(), count));

        } catch (Exception e) {
            log.error("Ошибка отправки метрики", e);
        }
    }
}
