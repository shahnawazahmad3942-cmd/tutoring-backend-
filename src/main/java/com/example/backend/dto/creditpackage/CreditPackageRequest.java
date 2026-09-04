package com.example.backend.dto.creditpackage;
/* Incoming payload for creating or updating a credit package. */
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreditPackageRequest(
     @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name should not exceed 255 character")
    String name,

    @Size(max = 1000, message = "Description should not exceed 1000 character")
    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater then 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer and 2 decimals")
    BigDecimal price,

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    Integer credits
) {
    
}
