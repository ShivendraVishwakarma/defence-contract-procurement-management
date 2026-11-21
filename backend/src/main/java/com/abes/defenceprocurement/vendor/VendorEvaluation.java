package com.abes.defenceprocurement.vendor;

import com.abes.defenceprocurement.contract.Vendor;
import com.abes.defenceprocurement.requisition.ProcurementRequisition;
import com.abes.defenceprocurement.user.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VENDOR_EVALUATIONS")
public class VendorEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVALUATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID", nullable = false)
    private ProcurementRequisition requisition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VENDOR_ID", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVALUATED_BY_USER_ID", nullable = false)
    private AppUser evaluatedBy;

    @Column(name = "SCORE", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "RECOMMENDATION", nullable = false, length = 1000)
    private String recommendation;

    @Column(name = "EVALUATED_AT", nullable = false)
    private LocalDateTime evaluatedAt;

    protected VendorEvaluation() { }

    @PrePersist
    void setEvaluatedAt() {
        if (evaluatedAt == null) evaluatedAt = LocalDateTime.now();
    }

    Long getId() { return id; }
    void setRequisition(ProcurementRequisition requisition) { this.requisition = requisition; }
    void setVendor(Vendor vendor) { this.vendor = vendor; }
    void setEvaluatedBy(AppUser evaluatedBy) { this.evaluatedBy = evaluatedBy; }
    void setScore(BigDecimal score) { this.score = score; }
    void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
