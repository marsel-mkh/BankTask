package com.t1.marselmkh.configuration;

import com.t1.marselmkh.aspect.HttpRequestLogAspects;
import com.t1.marselmkh.service.HttpRequestLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HttpRequestLogAspects.class)
public class HttpLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestLogAspects httpRequestLogAspects(HttpRequestLogService logService) {
        return new HttpRequestLogAspects(logService);
    }
}
