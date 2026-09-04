package com.example.backend.dto.creditpackage;

/* Outgoing representation of a credit package returned by the API. */

import java.math.BigDecimal;

public record CreditPackageResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    int credits,
    boolean active
) {
}
