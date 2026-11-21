package com.abes.defenceprocurement.invoice;

import com.abes.defenceprocurement.contract.*;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {
    private final InvoiceRepository invoices;
    private final InvoiceStatusRepository invoiceStatuses;
    private final PaymentStatusRepository paymentStatuses;
    private final PaymentRepository payments;
    private final ContractRepository contracts;

    public InvoiceService(InvoiceRepository invoices, InvoiceStatusRepository invoiceStatuses,
            PaymentStatusRepository paymentStatuses, PaymentRepository payments, ContractRepository contracts) {
        this.invoices = invoices; this.invoiceStatuses = invoiceStatuses; this.paymentStatuses = paymentStatuses;
        this.payments = payments; this.contracts = contracts;
    }

    @Transactional(readOnly = true) public List<InvoiceSummary> list() { return invoices.findAllSummaries(); }
    @Transactional(readOnly = true) public InvoiceOptions options() {
        return new InvoiceOptions(contracts.findAllForListing(), invoiceStatuses.findAllOptions(), paymentStatuses.findAllOptions());
    }
    @Transactional public InvoiceSummary create(InvoiceRequest request) {
        String number = request.invoiceNumber().trim();
        if (invoices.existsByInvoiceNumber(number)) throw new InvalidInvoiceException("Invoice number already exists");
        if (request.dueDate().isBefore(request.invoiceDate())) throw new InvalidInvoiceException("Due date must not precede invoice date");
        Invoice invoice = new Invoice(); apply(invoice, request);
        return saveAndRead(invoice);
    }
    @Transactional public InvoiceSummary changeStatus(Long id, InvoiceStatusRequest request) {
        Invoice invoice = invoices.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
        invoice.setStatus(status(request.statusCode()));
        return saveAndRead(invoice);
    }
    @Transactional public void addPayment(Long id, PaymentRequest request) {
        Invoice invoice = invoices.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
        Payment payment = new Payment(); payment.setInvoice(invoice);
        payment.setStatus(paymentStatuses.findByCode(request.statusCode().trim())
                .orElseThrow(() -> new InvalidInvoiceException("Payment status does not exist")));
        payment.setPaymentReference(request.paymentReference().trim()); payment.setPaymentDate(request.paymentDate());
        payment.setAmount(request.amount()); payment.setPaymentMethod(request.paymentMethod().trim());
        try { payments.saveAndFlush(payment); } catch (DataIntegrityViolationException exception) {
            throw new InvalidInvoiceException("Payment conflicts with existing data");
        }
    }
    private void apply(Invoice invoice, InvoiceRequest request) {
        invoice.setInvoiceNumber(request.invoiceNumber().trim());
        invoice.setContract(contracts.findById(request.contractId()).orElseThrow(() -> new InvalidInvoiceException("Contract does not exist")));
        invoice.setStatus(status(request.statusCode())); invoice.setInvoiceDate(request.invoiceDate());
        invoice.setDueDate(request.dueDate()); invoice.setTotalAmount(request.totalAmount());
    }
    private InvoiceStatus status(String code) {
        return invoiceStatuses.findByCode(code.trim()).orElseThrow(() -> new InvalidInvoiceException("Invoice status does not exist"));
    }
    private InvoiceSummary saveAndRead(Invoice invoice) {
        try { Long id = invoices.saveAndFlush(invoice).getId(); return invoices.findSummaryById(id).orElseThrow(() -> new InvoiceNotFoundException(id)); }
        catch (DataIntegrityViolationException exception) { throw new InvalidInvoiceException("Invoice conflicts with existing data"); }
    }
}
