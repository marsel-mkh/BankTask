package com.t1.marselmkh.service;

import com.t1.marselmkh.annotation.LogDatasourceError;
import com.t1.marselmkh.annotation.Metric;
import com.t1.marselmkh.dto.CardEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@LogDatasourceError
@RequiredArgsConstructor
public class CardService {
    private final CardKafkaProducer kafkaProducer;

    @Metric
    public void createCard(CardEventDto cardEventDto) {
        // throw new ClientNotFoundException("NOT FOUND");// starter test
        kafkaProducer.sendCard(cardEventDto);
    }
}
