package com.abes.defenceprocurement.requisition;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RequisitionItemRequest(
        @NotNull @Positive @Max(99999) Integer lineNumber,
        @NotBlank @Size(max = 240) String itemDescription,
        @NotNull @DecimalMin(value = "0.001") @Digits(integer = 9, fraction = 3) BigDecimal quantity,
        @NotBlank @Size(max = 30) String unitOfMeasure,
        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 14, fraction = 2) BigDecimal estimatedUnitCost) { }
