package com.abes.defenceprocurement.requisition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequisitionStatusRequest(@NotBlank @Size(max = 30) String statusCode) { }
