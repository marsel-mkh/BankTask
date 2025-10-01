package com.t1.marselmkh.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseLogEvent {
    OffsetDateTime timestamp;
    String serviceName;
    String methodSignature;
    List<Object> inputParameters;
}