package com.t1.marselmkh.configuration;

import com.t1.marselmkh.aspect.HttpRequestLogAspects;
import com.t1.marselmkh.properties.HttpIncomeLogProperties;
import com.t1.marselmkh.properties.HttpOutcomeLogProperties;
import com.t1.marselmkh.service.HttpRequestLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({HttpOutcomeLogProperties.class, HttpIncomeLogProperties.class})
public class HttpLoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestLogAspects httpRequestLogAspects(HttpRequestLogService logService,
                                                       HttpIncomeLogProperties incomeLogProperties,
                                                       HttpOutcomeLogProperties outcomeLogProperties) {
        return new HttpRequestLogAspects(logService, incomeLogProperties, outcomeLogProperties);
    }
}