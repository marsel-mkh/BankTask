package com.t1.marselmkh.configuration;


import com.t1.marselmkh.aspect.CachedAspect;
import com.t1.marselmkh.properties.CacheProperties;
import com.t1.marselmkh.service.CacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CachedAspect cachedAspect(CacheService cacheService) {
        return new CachedAspect(cacheService);
    }
}
