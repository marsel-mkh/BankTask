package com.t1.marselmkh.service;

import com.t1.marselmkh.annotation.Level;
import com.t1.marselmkh.dto.LogEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingProducer {
    private final KafkaTemplate<String, LogEventDto> kafkaTemplate;

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    @Value("${kafka.topic.service-log}")
    private String topic;

    public void sendToKafka(LogEventDto logEventDto, Level level) {

        Message<LogEventDto> message = MessageBuilder
                .withPayload(logEventDto)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("type", level.name())
                .setHeader(KafkaHeaders.KEY, serviceName)
                .build();
        try {
            kafkaTemplate.send(message);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения в Kafka: {}", message, e);
            throw e;
        }
    }
}
