package com.t1.marselmkh.configuration;

import com.t1.marselmkh.aspect.MetricAspect;
import com.t1.marselmkh.properties.MetricProperties;
import com.t1.marselmkh.service.MetricService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MetricProperties.class)
public class MetricAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetricAspect logDatasourceErrorAspect(MetricService service, MetricProperties properties) {
        return new MetricAspect(properties, service);
    }
}