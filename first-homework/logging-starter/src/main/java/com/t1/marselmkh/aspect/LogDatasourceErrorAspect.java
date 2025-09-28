package com.t1.marselmkh.aspect;

import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.annotation.LogDatasourceError;
import com.t1.marselmkh.dto.LogEventDto;
import com.t1.marselmkh.service.ErrorLoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDatasourceErrorAspect {

    private final ErrorLoggingService errorLoggingService;

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @AfterThrowing(
            pointcut = "@annotation(com.t1.marselmkh.annotation.LogDatasourceError)" +
                    " || @within(com.t1.marselmkh.annotation.LogDatasourceError)",
            throwing = "ex"
    )
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        Level level = resolveLevel(joinPoint);
        LogEventDto logEventDto = LogEventDto.builder()
                .timestamp(OffsetDateTime.now())
                .serviceName(serviceName)
                .methodSignature(joinPoint.getSignature().toShortString())
                .exceptionMessage(ex.getMessage())
                .stackTrace(ex.toString())
                .inputParameters( Arrays.asList(joinPoint.getArgs()))
                .build();
        errorLoggingService.sendOrPersist(logEventDto, level);

    }

    private Level resolveLevel(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogDatasourceError annotation = method.getAnnotation(LogDatasourceError.class);
        if (annotation != null) {
            return annotation.value();
        }

        annotation = joinPoint.getTarget().getClass().getAnnotation(LogDatasourceError.class);

        if (annotation != null) {
            return annotation.value();
        }

        return Level.ERROR;
    }
}
