package com.t1.marselmkh.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    Long accountId;
    BigDecimal amount;
    LocalDateTime paymentDate;
}
