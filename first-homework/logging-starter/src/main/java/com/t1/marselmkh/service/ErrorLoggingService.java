package com.t1.marselmkh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.dto.BaseLogEvent;
import com.t1.marselmkh.dto.ErrorLogEventDto;
import com.t1.marselmkh.properties.ErrorLogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLoggingService {

    private final LoggingProducer loggingProducer;
    private final ObjectMapper objectMapper;
    private final ErrorLogProperties errorLogProperties;
    private final ErrorLogPersistenceService errorLogPersistenceService;

    public void sendOrPersist(ErrorLogEventDto logEventDto, Level level) {
        String payload = serialize(logEventDto);
        boolean flag = false;

        try {
            if (errorLogProperties.isSendToKafka())
                loggingProducer.sendToKafka(logEventDto, level);
        } catch (Exception e) {
            log.error("Ошибка отправки ErrorLogEventDto в Kafka: method= {},", logEventDto.getMethodSignature());
            flag = true;
        } finally {
            if (errorLogProperties.isSaveToDatabase()) {
                log.info("Попытка сохранить ErrorLogEventDto в базу данных");
                errorLogPersistenceService.saveToDb(logEventDto, payload, flag);
                log.info("ErrorLog сохранён в базе данных.  method= {}", logEventDto.getMethodSignature());
            }
        }
    }

    private String serialize(BaseLogEvent logEventDto) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(logEventDto);
            log.debug("Успешно сериализован ErrorLogEventDto ");
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации ErrorLogEventDto ", e);
        }
        return payload;
    }
}