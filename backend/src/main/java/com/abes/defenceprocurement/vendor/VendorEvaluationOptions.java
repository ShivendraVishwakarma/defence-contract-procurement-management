package com.abes.defenceprocurement.vendor;

import com.abes.defenceprocurement.contract.VendorOption;
import com.abes.defenceprocurement.requisition.RequisitionSummary;
import java.util.List;

public record VendorEvaluationOptions(List<VendorOption> vendors, List<RequisitionSummary> requisitions) { }
