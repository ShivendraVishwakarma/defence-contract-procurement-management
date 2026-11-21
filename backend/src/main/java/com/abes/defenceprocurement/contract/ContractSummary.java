package com.abes.defenceprocurement.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractSummary(
        Long id,
        String contractNumber,
        String title,
        Long vendorId,
        String vendorName,
        String statusCode,
        String statusName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate endDate,
        BigDecimal contractValue,
        String summary) { }
