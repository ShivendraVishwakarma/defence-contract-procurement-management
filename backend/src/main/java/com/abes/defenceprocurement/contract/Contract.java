package com.abes.defenceprocurement.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CONTRACTS", uniqueConstraints =
        @UniqueConstraint(name = "UK_CONTRACTS_NUMBER", columnNames = "CONTRACT_NUMBER"))
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTRACT_ID")
    private Long id;

    @Column(name = "CONTRACT_NUMBER", nullable = false, length = 40)
    private String contractNumber;

    @Column(name = "TITLE", nullable = false, length = 180)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VENDOR_ID", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONTRACT_STATUS_ID", nullable = false)
    private ContractStatus status;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Column(name = "CONTRACT_VALUE", nullable = false, precision = 16, scale = 2)
    private BigDecimal contractValue;

    @Column(name = "SUMMARY", nullable = false, length = 1000)
    private String summary;

    protected Contract() { }

    void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    void setId(Long id) { this.id = id; }
    void setTitle(String title) { this.title = title; }
    void setVendor(Vendor vendor) { this.vendor = vendor; }
    void setStatus(ContractStatus status) { this.status = status; }
    void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    void setContractValue(BigDecimal contractValue) { this.contractValue = contractValue; }
    void setSummary(String summary) { this.summary = summary; }

    Long getId() { return id; }
    String getContractNumber() { return contractNumber; }
    String getTitle() { return title; }
    Vendor getVendor() { return vendor; }
    ContractStatus getStatus() { return status; }
    LocalDate getStartDate() { return startDate; }
    LocalDate getEndDate() { return endDate; }
    BigDecimal getContractValue() { return contractValue; }
    String getSummary() { return summary; }
}
