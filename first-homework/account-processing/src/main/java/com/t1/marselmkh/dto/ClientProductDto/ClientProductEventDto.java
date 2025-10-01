package com.t1.marselmkh.dto.ClientProductDto;

import com.t1.marselmkh.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientProductEventDto {

    @NotNull(message = "clientId is required")
    String clientId;

    @NotNull(message = "productId is required")
    String productId;

    @NotBlank(message = "status cannot be blank")
    Status status;
}
