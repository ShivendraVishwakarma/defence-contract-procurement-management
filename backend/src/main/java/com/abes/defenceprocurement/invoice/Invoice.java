package com.abes.defenceprocurement.invoice;

import com.abes.defenceprocurement.contract.Contract;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INVOICES")
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "INVOICE_ID")
    private Long id;
    @Column(name = "INVOICE_NUMBER", nullable = false, length = 50) private String invoiceNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "CONTRACT_ID") private Contract contract;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "INVOICE_STATUS_ID") private InvoiceStatus status;
    @Column(name = "INVOICE_DATE", nullable = false) private LocalDate invoiceDate;
    @Column(name = "DUE_DATE", nullable = false) private LocalDate dueDate;
    @Column(name = "TOTAL_AMOUNT", nullable = false, precision = 16, scale = 2) private BigDecimal totalAmount;
    protected Invoice() { }
    Long getId() { return id; }
    void setInvoiceNumber(String value) { invoiceNumber = value; }
    void setContract(Contract value) { contract = value; }
    void setStatus(InvoiceStatus value) { status = value; }
    void setInvoiceDate(LocalDate value) { invoiceDate = value; }
    void setDueDate(LocalDate value) { dueDate = value; }
    void setTotalAmount(BigDecimal value) { totalAmount = value; }
}
