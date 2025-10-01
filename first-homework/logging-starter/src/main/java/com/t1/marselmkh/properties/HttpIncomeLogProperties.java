package com.t1.marselmkh.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.http.income")
public class HttpIncomeLogProperties {
    /**
     * Включить/выключить логирование входящих запросов.
     */
    private boolean enabled = true;

    /**
     * Отправлять ли событие в Kafka.
     */
    private boolean sendToKafka = true;
}