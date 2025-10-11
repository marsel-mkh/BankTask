package com.t1.marselmkh.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "logging.metric")
public class MetricProperties {

    /**
     * Включить/выключить метрики.
     */
    private boolean enabled = true;

    /**
     * Лимит времени выполнения метода (мс).
     */
    private long limitMs = 500;


}