package com.t1.marselmkh.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogEventDto {

    OffsetDateTime timestamp;
    String serviceName;
    String methodSignature;
    String exceptionMessage;
    String stackTrace;
    List<Object> inputParameters;
    String response;
    String uri;
}
