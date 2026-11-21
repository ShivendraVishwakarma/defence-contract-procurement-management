package com.abes.defenceprocurement.vendor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendorEvaluationSummary(
        Long id,
        Long requisitionId,
        String requisitionNumber,
        Long vendorId,
        String vendorCode,
        String vendorName,
        Long evaluatedByUserId,
        String evaluatedByName,
        BigDecimal score,
        String recommendation,
        @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime evaluatedAt) { }
