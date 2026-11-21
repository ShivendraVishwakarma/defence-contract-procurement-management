package com.abes.defenceprocurement.contract;

import java.util.List;

public record ContractOptions(List<VendorOption> vendors, List<ContractStatusOption> statuses) { }
