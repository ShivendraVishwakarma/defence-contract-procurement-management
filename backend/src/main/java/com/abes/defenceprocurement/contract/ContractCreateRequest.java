package com.abes.defenceprocurement.contract;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractCreateRequest(
        @NotBlank @Size(max = 40) String contractNumber,
        @NotBlank @Size(max = 180) String title,
        @NotNull @Positive Long vendorId,
        @NotBlank @Size(max = 30) String statusCode,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 14, fraction = 2)
        BigDecimal contractValue,
        @NotBlank @Size(max = 1000) String summary) {
    @AssertTrue(message = "End date must be on or after start date")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
