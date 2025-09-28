package com.t1.marselmkh.aspect;


import com.t1.marselmkh.dto.LogEventDto;
import com.t1.marselmkh.service.HttpRequestLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HttpRequestLogAspects {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    private final HttpRequestLogService httpRequestLogService;

    /**
     * Исходящие HTTP-запросы (AfterReturning)
     */
    @AfterReturning(
            pointcut = "@annotation(com.t1.marselmkh.annotation.HttpOutcomeRequestLog) || " +
                    "@within(com.t1.marselmkh.annotation.HttpOutcomeRequestLog)",
            returning = "result"
    )
    public void logHttpOutcomeRequest(JoinPoint joinPoint, Object result) {
        LogEventDto dto = createDto(joinPoint);

        if (result instanceof ResponseEntity<?> responseEntity) {
            dto.setResponse(responseEntity.getBody() != null
                    ? responseEntity.getBody().toString()
                    : "null");
        }else {
            dto.setResponse(result != null ? result.toString() : "null");
        }
        httpRequestLogService.sendToKafka(dto);
    }

    /**
     * Входящие HTTP-запросы (Before)
     */
    @Before("@annotation(com.t1.marselmkh.annotation.HttpIncomeRequestLog) || " +
            "@within(com.t1.marselmkh.annotation.HttpIncomeRequestLog)"
    )
    public void logHttpIncomeRequest(JoinPoint joinPoint) {
        LogEventDto dto = createDto(joinPoint);
        httpRequestLogService.sendToKafka(dto);
    }

    private LogEventDto createDto(JoinPoint joinPoint) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();

        return LogEventDto.builder()
                .timestamp(OffsetDateTime.now())
                .serviceName(serviceName)
                .methodSignature(joinPoint.getSignature().toShortString())
                .inputParameters(Arrays.stream(joinPoint.getArgs())
                        .map(arg -> arg != null ? arg.toString() : "null")
                        .collect(Collectors.toList()))
                .uri(method + " " + uri)
                .build();
    }
}
