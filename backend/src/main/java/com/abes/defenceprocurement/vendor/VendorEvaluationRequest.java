package com.abes.defenceprocurement.vendor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VendorEvaluationRequest(
        @NotNull Long requisitionId,
        @NotNull Long vendorId,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal score,
        @NotBlank @Size(max = 1000) String recommendation) { }
