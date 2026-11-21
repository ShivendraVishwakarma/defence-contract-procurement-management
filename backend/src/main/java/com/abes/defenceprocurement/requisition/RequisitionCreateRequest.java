package com.abes.defenceprocurement.requisition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RequisitionCreateRequest(
        @NotBlank @Size(max = 40) String requisitionNumber,
        @NotBlank @Size(max = 180) String title,
        @NotBlank @Size(max = 1000) String justification,
        @NotNull LocalDate neededByDate,
        @NotBlank @Size(max = 30) String statusCode) { }
