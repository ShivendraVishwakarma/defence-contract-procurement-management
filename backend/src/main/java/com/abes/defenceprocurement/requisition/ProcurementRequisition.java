package com.abes.defenceprocurement.requisition;

import com.abes.defenceprocurement.user.AppUser;
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
import java.time.LocalDate;

@Entity
@Table(name = "PROCUREMENT_REQUISITIONS", uniqueConstraints =
        @UniqueConstraint(name = "UK_REQUISITIONS_NUMBER", columnNames = "REQUISITION_NUMBER"))
public class ProcurementRequisition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUISITION_ID")
    private Long id;

    @Column(name = "REQUISITION_NUMBER", nullable = false, length = 40)
    private String requisitionNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUESTED_BY_USER_ID", nullable = false)
    private AppUser requestedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_STATUS_ID", nullable = false)
    private RequisitionStatus status;

    @Column(name = "TITLE", nullable = false, length = 180)
    private String title;

    @Column(name = "JUSTIFICATION", nullable = false, length = 1000)
    private String justification;

    @Column(name = "NEEDED_BY_DATE", nullable = false)
    private LocalDate neededByDate;

    protected ProcurementRequisition() { }

    void setId(Long id) { this.id = id; }
    void setRequisitionNumber(String requisitionNumber) { this.requisitionNumber = requisitionNumber; }
    void setRequestedBy(AppUser requestedBy) { this.requestedBy = requestedBy; }
    void setStatus(RequisitionStatus status) { this.status = status; }
    void setTitle(String title) { this.title = title; }
    void setJustification(String justification) { this.justification = justification; }
    void setNeededByDate(LocalDate neededByDate) { this.neededByDate = neededByDate; }

    Long getId() { return id; }
    String getRequisitionNumber() { return requisitionNumber; }
    AppUser getRequestedBy() { return requestedBy; }
    RequisitionStatus getStatus() { return status; }
    String getTitle() { return title; }
    String getJustification() { return justification; }
    LocalDate getNeededByDate() { return neededByDate; }
}
