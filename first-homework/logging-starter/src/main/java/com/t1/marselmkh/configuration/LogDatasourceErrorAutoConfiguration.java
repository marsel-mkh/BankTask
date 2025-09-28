package com.t1.marselmkh.configuration;

import com.t1.marselmkh.aspect.LogDatasourceErrorAspect;
import com.t1.marselmkh.service.ErrorLoggingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LogDatasourceErrorAspect.class)
public class LogDatasourceErrorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogDatasourceErrorAspect logDatasourceErrorAspect(ErrorLoggingService errorLoggingService) {
        return new LogDatasourceErrorAspect(errorLoggingService);
    }
}
