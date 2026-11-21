package com.abes.defenceprocurement.invoice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAYMENTS")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "PAYMENT_ID") private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "INVOICE_ID") private Invoice invoice;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "PAYMENT_STATUS_ID") private PaymentStatus status;
    @Column(name = "PAYMENT_REFERENCE", nullable = false, length = 50) private String paymentReference;
    @Column(name = "PAYMENT_DATE", nullable = false) private LocalDate paymentDate;
    @Column(name = "AMOUNT", nullable = false, precision = 16, scale = 2) private BigDecimal amount;
    @Column(name = "PAYMENT_METHOD", nullable = false, length = 30) private String paymentMethod;
    protected Payment() { }
    void setInvoice(Invoice value) { invoice = value; }
    void setStatus(PaymentStatus value) { status = value; }
    void setPaymentReference(String value) { paymentReference = value; }
    void setPaymentDate(LocalDate value) { paymentDate = value; }
    void setAmount(BigDecimal value) { amount = value; }
    void setPaymentMethod(String value) { paymentMethod = value; }
}
