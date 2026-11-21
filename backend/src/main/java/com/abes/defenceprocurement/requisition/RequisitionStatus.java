package com.abes.defenceprocurement.requisition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "REQUISITION_STATUSES", uniqueConstraints =
        @UniqueConstraint(name = "UK_REQUISITION_STATUSES_CODE", columnNames = "STATUS_CODE"))
public class RequisitionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUISITION_STATUS_ID")
    private Long id;

    @Column(name = "STATUS_CODE", nullable = false, length = 30)
    private String code;

    @Column(name = "STATUS_NAME", nullable = false, length = 80)
    private String name;

    protected RequisitionStatus() { }

    Long getId() { return id; }
    String getCode() { return code; }
    String getName() { return name; }
}
