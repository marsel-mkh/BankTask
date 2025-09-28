package com.t1.marselmkh.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.dto.LogEventDto;
import com.t1.marselmkh.entity.ErrorLog;
import com.t1.marselmkh.mapper.ErrorLogMapper;
import com.t1.marselmkh.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLoggingService {

    private final ErrorLogRepository errorLogRepository;
    private final LoggingProducer loggingProducer;
    private final ObjectMapper objectMapper;
    private final ErrorLogMapper errorLogMapper;

    public void sendOrPersist(LogEventDto logEventDto, Level level) {
        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(logEventDto);
            log.debug("Успешно сериализован ErrorLogEventDto: {}", payload);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации ErrorLogEventDto: {}", logEventDto, e);
        }

        ErrorLog errorLog = errorLogMapper.toEntity(logEventDto);
        try {
            loggingProducer.sendToKafka(logEventDto, level);
        } catch (Exception e) {
            log.error("Ошибка отправки ErrorLogEventDto в Kafka: {}", logEventDto, e);
            errorLog.setPayload(payload);
        } finally {
            errorLogRepository.save(errorLog);
            log.info("ErrorLog сохранён в базе данных: {}", errorLog);
        }
    }
}