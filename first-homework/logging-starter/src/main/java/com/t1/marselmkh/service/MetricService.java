package com.t1.marselmkh.service;

import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.dto.MetricLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricService {
    private final LoggingProducer loggingProducer;

    public void sendToKafka(MetricLogEvent eventDto) {
        try {
            loggingProducer.sendToKafka(eventDto, Level.WARNING);
            log.info("Успешная отправка метрики в Kafka");
        } catch (Exception e) {
            log.error("Ошибка отправки метрики в Kafka {}", e.getMessage());
        }
    }
}