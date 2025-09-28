package com.t1.marselmkh.internal;

import com.t1.marselmkh.service.HttpRequestLogService;
import com.t1.marselmkh.service.LoggingProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpRequestLogServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestLogService httpRequestLogService(LoggingProducer loggingProducer) {
        return new HttpRequestLogService(loggingProducer);
    }
}
