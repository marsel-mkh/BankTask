package com.t1.marselmkh.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.mapper.ErrorLogMapper;
import com.t1.marselmkh.repository.ErrorLogRepository;
import com.t1.marselmkh.service.ErrorLoggingService;
import com.t1.marselmkh.service.LoggingProducer;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorLoggingServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ErrorLoggingService errorLoggingService(ErrorLogRepository errorLogRepository,
                                                   LoggingProducer loggingProducer,
                                                   ObjectMapper objectMapper,
                                                   ErrorLogMapper errorLogMapper) {
        return new ErrorLoggingService(errorLogRepository,
                loggingProducer,
                objectMapper,
                errorLogMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorLogMapper errorLogMapper() {
        return Mappers.getMapper(ErrorLogMapper.class);
    }


}
