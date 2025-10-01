package com.t1.marselmkh.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.dto.BaseLogEvent;
 import com.t1.marselmkh.service.LoggingProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LoggingProducerConfiguration {

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public LoggingProducer loggingProducer(KafkaTemplate<String, BaseLogEvent> kafkaTemplate) {
        return new LoggingProducer(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, BaseLogEvent> logEventKafkaTemplate(
            ProducerFactory<String, BaseLogEvent> logEventProducerFactory) {
        return new KafkaTemplate<>(logEventProducerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, BaseLogEvent> logEventProducerFactory(
            KafkaProperties kafkaProperties, ObjectMapper objectMapper) {

        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper));
    }
}