package com.t1.marselmkh.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.mapper.ErrorLogMapper;
import com.t1.marselmkh.properties.ErrorLogProperties;
import com.t1.marselmkh.repository.ErrorLogRepository;
import com.t1.marselmkh.service.ErrorLogPersistenceService;
import com.t1.marselmkh.service.ErrorLoggingService;
import com.t1.marselmkh.service.LoggingProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorLoggingServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ErrorLoggingService errorLoggingService(LoggingProducer loggingProducer,
                                                   ObjectMapper objectMapper,
                                                   ErrorLogProperties errorLogProperties,
                                                   ErrorLogPersistenceService errorLogPersistenceService) {
        return new ErrorLoggingService(loggingProducer,
                objectMapper,
                errorLogProperties,
                errorLogPersistenceService
        );
    }

    @Bean
    @ConditionalOnClass(ErrorLogRepository.class)
    public ErrorLogPersistenceService errorLogPersistenceService(ErrorLogRepository repository,
                                                                 ErrorLogMapper mapper) {
        return new ErrorLogPersistenceService(repository, mapper);

    }
}