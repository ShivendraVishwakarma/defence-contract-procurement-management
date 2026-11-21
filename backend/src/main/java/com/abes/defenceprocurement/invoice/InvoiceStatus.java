package com.abes.defenceprocurement.invoice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "INVOICE_STATUSES")
public class InvoiceStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "INVOICE_STATUS_ID") private Long id;
    @Column(name = "STATUS_CODE", nullable = false) private String code;
    @Column(name = "STATUS_NAME", nullable = false) private String name;
    protected InvoiceStatus() { }
}
