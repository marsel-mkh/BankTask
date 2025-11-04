package com.t1.marselmkh.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    /**
     * TTL по умолчанию в секундах
     */
    private long ttlSeconds = 60;
}
