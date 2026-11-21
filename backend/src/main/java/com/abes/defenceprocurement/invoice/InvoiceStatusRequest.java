package com.abes.defenceprocurement.invoice;

import jakarta.validation.constraints.NotBlank;

public record InvoiceStatusRequest(@NotBlank String statusCode) { }
