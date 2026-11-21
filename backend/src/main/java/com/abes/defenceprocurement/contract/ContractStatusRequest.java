package com.abes.defenceprocurement.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContractStatusRequest(@NotBlank @Size(max = 30) String statusCode) { }
