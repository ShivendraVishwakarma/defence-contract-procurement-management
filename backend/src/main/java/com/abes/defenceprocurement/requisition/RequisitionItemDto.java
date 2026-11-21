package com.abes.defenceprocurement.requisition;

import java.math.BigDecimal;

public record RequisitionItemDto(Long id, Integer lineNumber, String itemDescription,
                                 BigDecimal quantity, String unitOfMeasure, BigDecimal estimatedUnitCost) { }
