package com.t1.marselmkh.dto.ProductDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateDto {

    @NotBlank(message = "Name is mandatory")
    private String name;
}
