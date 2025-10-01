package com.t1.marselmkh.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.http.outcome")
public class HttpOutcomeLogProperties {
    /**
     * Включить/выключить логирование исходящих запросов.
     */
    private boolean enabled = true;

    /**
     * Отправлять ли событие в Kafka.
     */
    private boolean sendToKafka = true;

}