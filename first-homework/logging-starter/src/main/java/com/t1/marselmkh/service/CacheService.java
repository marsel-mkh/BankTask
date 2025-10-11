package com.t1.marselmkh.service;

import com.t1.marselmkh.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(
                key,
                value,
                cacheProperties.getTtlSeconds(),
                TimeUnit.SECONDS
        );
    }

    public String generateKey(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        if (args.length == 0) {
            return String.format("%s:%s", className, methodName);
        }
        Object arg = args[0];
        if (arg == null) {
            return String.format("%s:%s:%s", className, methodName, ":null");
        } else if (BeanUtils.isSimpleValueType(arg.getClass())) {
            return String.format("%s:%s:%s", className, methodName, arg);
        } else {
            return String.format("%s:%s:%s", className, methodName, arg.hashCode());
        }
    }
}
