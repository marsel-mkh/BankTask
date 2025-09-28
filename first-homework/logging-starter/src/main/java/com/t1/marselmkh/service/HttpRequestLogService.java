package com.t1.marselmkh.service;

import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.dto.LogEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpRequestLogService {
    private final LoggingProducer loggingProducer;


    public void sendToKafka(LogEventDto eventDto) {
        try {
            loggingProducer.sendToKafka(eventDto, Level.INFO);
            log.info("Успешная отправка в Kafka HttpOutcomeRequestLog c uri: {}",
                    eventDto.getUri());
        } catch (Exception e) {
            log.error("Ошибка отправки HttpOutcomeRequestLog  в Kafka ", e);
        }
    }
}
