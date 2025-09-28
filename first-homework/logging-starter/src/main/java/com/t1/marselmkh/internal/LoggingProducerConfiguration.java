package com.t1.marselmkh.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t1.marselmkh.dto.LogEventDto;
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
    public LoggingProducer loggingProducer(KafkaTemplate<String, LogEventDto> kafkaTemplate) {
        return new LoggingProducer(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "logEventKafkaTemplate")
    public KafkaTemplate<String, LogEventDto> logEventKafkaTemplate(
            ProducerFactory<String, LogEventDto> logEventProducerFactory) {
        return new KafkaTemplate<>(logEventProducerFactory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "logEventProducerFactory")
    public ProducerFactory<String, LogEventDto> logEventProducerFactory(
            KafkaProperties kafkaProperties, ObjectMapper objectMapper) {

        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper));
    }
}
