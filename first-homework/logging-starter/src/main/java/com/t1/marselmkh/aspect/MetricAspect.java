package com.t1.marselmkh.aspect;

import com.t1.marselmkh.dto.MetricLogEvent;
import com.t1.marselmkh.properties.MetricProperties;
import com.t1.marselmkh.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {
    private final MetricProperties metricProperties;
    private final MetricService metricService;


    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @Around("@annotation(com.t1.marselmkh.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        if (!metricProperties.isEnabled()) {
            return pjp.proceed();
        }

        long executionTime;
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            executionTime = System.currentTimeMillis() - start;
        }

        if (executionTime > metricProperties.getLimitMs()) {
            MetricLogEvent event = MetricLogEvent.builder()
                    .timestamp(OffsetDateTime.now())
                    .executionTimeMs(executionTime)
                    .methodSignature(pjp.getSignature().toShortString())
                    .inputParameters(Arrays.stream(pjp.getArgs())
                            .map(arg -> arg != null ? arg.toString() : "null")
                            .collect(Collectors.toList()))
                    .serviceName(serviceName)
                    .build();

            metricService.sendToKafka(event);
        }
        return result;
    }
}