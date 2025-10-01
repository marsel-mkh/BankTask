package com.t1.marselmkh.configuration;

import com.t1.marselmkh.aspect.LogErrorAspect;
import com.t1.marselmkh.properties.ErrorLogProperties;
import com.t1.marselmkh.service.ErrorLoggingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ErrorLogProperties.class)
public class LogErrorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogErrorAspect logErrorAspect(ErrorLoggingService errorLoggingService,
                                         ErrorLogProperties errorLogProperties) {
        return new LogErrorAspect(errorLoggingService, errorLogProperties);
    }
}