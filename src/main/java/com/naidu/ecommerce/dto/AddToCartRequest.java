package com.naidu.ecommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class AddToCartRequest {

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotNull(message = "productId is required")
    private Long productId;

    @Positive(message = "quantity must be greater than 0")
    private Integer quantity;
}
