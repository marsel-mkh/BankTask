package com.t1.marselmkh.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CachedAspect {

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.t1.marselmkh.annotation.Cached)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = cacheService.generateKey(joinPoint);

        Object cachedValue = cacheService.get(key);

        if (cachedValue != null) {
            log.debug("Возвращаем из Redis кэша: {}", key);
            return objectMapper.convertValue(cachedValue, ((MethodSignature) joinPoint.getSignature()).getReturnType());
        }

        Object result = joinPoint.proceed();

        if (result != null) {
            cacheService.put(key, result);
            log.debug("Кладем кеш в Redis: {}", key);
        }

        return result;
    }
}
