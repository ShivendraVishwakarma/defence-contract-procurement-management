package com.abes.defenceprocurement.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "VENDORS", uniqueConstraints =
        @UniqueConstraint(name = "UK_VENDORS_CODE", columnNames = "VENDOR_CODE"))
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VENDOR_ID")
    private Long id;

    @Column(name = "VENDOR_CODE", nullable = false, length = 30)
    private String code;

    @Column(name = "VENDOR_NAME", nullable = false, length = 160)
    private String name;

    protected Vendor() { }

    Long getId() { return id; }
    String getCode() { return code; }
    String getName() { return name; }
}
