package com.t1.marselmkh.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.error")
public class ErrorLogProperties {
    /**
     * Включить/выключить логирование ошибок.
     */
    private boolean enabled = true;

    /**
     * Отправлять ли ошибки в Kafka.
     */
    private boolean sendToKafka = true;

    /**
     * Сохранять ли ошибки в базу данных.
     */
    private boolean saveToDatabase = true;
}