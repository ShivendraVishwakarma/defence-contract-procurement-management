package com.abes.defenceprocurement.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceSummary(Long id, String invoiceNumber, Long contractId, String contractNumber,
        String statusCode, String statusName, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate invoiceDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dueDate,
        BigDecimal totalAmount) { }
