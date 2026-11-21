package com.abes.defenceprocurement.invoice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.abes.defenceprocurement.contract.Contract;
import com.abes.defenceprocurement.contract.ContractRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InvoiceServiceTest {
    private final InvoiceRepository invoices = mock(InvoiceRepository.class);
    private final InvoiceStatusRepository invoiceStatuses = mock(InvoiceStatusRepository.class);
    private final PaymentStatusRepository paymentStatuses = mock(PaymentStatusRepository.class);
    private final PaymentRepository payments = mock(PaymentRepository.class);
    private final ContractRepository contracts = mock(ContractRepository.class);
    private final InvoiceService service = new InvoiceService(invoices, invoiceStatuses, paymentStatuses, payments, contracts);

    @Test
    void rejectsDueDateBeforeInvoiceDate() {
        InvoiceRequest request = new InvoiceRequest("INV-1", 2L, "ISSUED",
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 19), BigDecimal.TEN);
        assertThrows(InvalidInvoiceException.class, () -> service.create(request));
        verifyNoInteractions(contracts, invoiceStatuses);
    }

    @Test
    void rejectsDuplicateInvoiceNumber() {
        when(invoices.existsByInvoiceNumber("INV-1")).thenReturn(true);
        InvoiceRequest request = new InvoiceRequest(" INV-1 ", 2L, "ISSUED",
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 21), BigDecimal.TEN);
        assertThrows(InvalidInvoiceException.class, () -> service.create(request));
        verifyNoInteractions(contracts, invoiceStatuses);
    }
}
