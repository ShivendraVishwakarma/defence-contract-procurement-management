package com.abes.defenceprocurement.invoice;

import jakarta.persistence.*;

@Entity
@Table(name = "PAYMENT_STATUSES")
public class PaymentStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "PAYMENT_STATUS_ID") private Long id;
    @Column(name = "STATUS_CODE", nullable = false) private String code;
    @Column(name = "STATUS_NAME", nullable = false) private String name;
    protected PaymentStatus() { }
}
