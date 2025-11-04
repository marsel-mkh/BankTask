package com.t1.marselmkh.dto;

import com.t1.marselmkh.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDto {

    @NotNull(message = "accountId is required")
    Long accountId;

    @NotBlank(message = "cardId cannot be blank")
    Long cardId;

    @NotBlank(message = "type cannot be blank")
    TransactionType type;

    @NotNull(message = "amount is required")
    BigDecimal amount;

    @NotNull(message = "date is required")
    LocalDateTime date;
}
