package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.CardEventDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.dto.PaymentDto;
import com.t1.marselmkh.dto.TransactionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardService cardService;
    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka.topic.client-products}", groupId = "${spring.kafka.consumer.group-id}")
    public void productConsumer(@Valid ClientProductEventDto clientProductEventDto) {
        log.info("Получено событие client_products: {}", clientProductEventDto);
        accountService.createAccount(clientProductEventDto);
    }

    @KafkaListener(topics = "${kafka.topic.client-transaction}", groupId = "${spring.kafka.consumer.group-id}")
    public void transactionConsumer(ConsumerRecord<UUID, TransactionDto> record) {
        UUID key = record.key();
        TransactionDto value = record.value();
        log.info("Получено событие client_transactions с ключом {}: {}", record.key(), record.value());
        transactionService.processTransaction(value);
    }

    @KafkaListener(topics = "${kafka.topic.client-cards}", groupId = "${spring.kafka.consumer.group-id}")
    public void cardConsumer(@Valid CardEventDto cardEventDto) {
        log.info("Получено событие client_cards: {}", cardEventDto);
        cardService.createCard(cardEventDto);
    }

    @KafkaListener(topics = "${kafka.topic.client-payments}", groupId = "${spring.kafka.consumer.group-id}")
    public void clientPaymentConsumer(ConsumerRecord<UUID, PaymentDto> record) {
        UUID key = record.key();
        PaymentDto value = record.value();
        log.info("Получено событие client-payments с ключом {}: {}", record.key(), record.value());
        paymentService.processPayment(value);
    }
}