package com.t1.marselmkh.internal;

import com.t1.marselmkh.service.LoggingProducer;
import com.t1.marselmkh.service.MetricService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetricService metricService(LoggingProducer loggingProducer) {
        return new MetricService(loggingProducer);
    }
}