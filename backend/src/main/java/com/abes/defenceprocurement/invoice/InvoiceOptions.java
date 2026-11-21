package com.abes.defenceprocurement.invoice;

import com.abes.defenceprocurement.contract.ContractSummary;
import java.util.List;

public record InvoiceOptions(List<ContractSummary> contracts, List<InvoiceStatusOption> invoiceStatuses,
        List<PaymentStatusOption> paymentStatuses) { }
