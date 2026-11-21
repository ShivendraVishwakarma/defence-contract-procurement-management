package com.abes.defenceprocurement.invoice;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceRequest(@NotBlank @Size(max = 50) String invoiceNumber, @NotNull Long contractId,
        @NotBlank String statusCode, @NotNull @PastOrPresent LocalDate invoiceDate, @NotNull LocalDate dueDate,
        @NotNull @DecimalMin("0.01") BigDecimal totalAmount) { }
