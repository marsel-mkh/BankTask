package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProductProducer {
    private final KafkaTemplate<String, ClientProductEventDto> kafkaTemplate;

    public void send(String topic, ClientProductEventDto eventDto) {
        kafkaTemplate.send(topic, eventDto);
        log.info("Отправлено событие в Kafka. topic={}, event={}", topic, eventDto);

    }
}
