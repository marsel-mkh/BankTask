package com.t1.marselmkh.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp
) {}