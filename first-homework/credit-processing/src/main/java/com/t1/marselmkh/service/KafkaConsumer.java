package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final CreditProcessingService creditProcessingService;

    @KafkaListener(topics ="${kafka.topic.credit-products}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void creditConsumer(@Valid ClientProductEventDto clientProductEventDto) {
        log.info("Получено событие из Kafka: {}", clientProductEventDto);
        creditProcessingService.processCredit(clientProductEventDto);
        log.info("Событие обработано: {}", clientProductEventDto);
    }
}
