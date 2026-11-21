package com.abes.defenceprocurement.invoice;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(@NotBlank @Size(max = 50) String paymentReference, @NotBlank String statusCode,
        @NotNull @PastOrPresent LocalDate paymentDate, @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(max = 30) String paymentMethod) { }
