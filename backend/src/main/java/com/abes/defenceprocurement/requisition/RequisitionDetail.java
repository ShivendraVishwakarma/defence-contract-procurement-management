package com.abes.defenceprocurement.requisition;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public record RequisitionDetail(
        Long id,
        String requisitionNumber,
        Long requesterId,
        String requesterName,
        String statusCode,
        String statusName,
        String title,
        String justification,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate neededByDate,
        List<RequisitionItemDto> items) { }
