package com.abes.defenceprocurement.requisition;

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

@Entity
@Table(name = "REQUISITION_ITEMS", uniqueConstraints =
        @UniqueConstraint(name = "UK_REQUISITION_LINE", columnNames = {"REQUISITION_ID", "LINE_NUMBER"}))
public class RequisitionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUISITION_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REQUISITION_ID", nullable = false)
    private ProcurementRequisition requisition;

    @Column(name = "LINE_NUMBER", nullable = false, precision = 5)
    private Integer lineNumber;

    @Column(name = "ITEM_DESCRIPTION", nullable = false, length = 240)
    private String itemDescription;

    @Column(name = "QUANTITY", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "UNIT_OF_MEASURE", nullable = false, length = 30)
    private String unitOfMeasure;

    @Column(name = "ESTIMATED_UNIT_COST", nullable = false, precision = 16, scale = 2)
    private BigDecimal estimatedUnitCost;

    protected RequisitionItem() { }

    void setRequisition(ProcurementRequisition requisition) { this.requisition = requisition; }
    void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    void setEstimatedUnitCost(BigDecimal estimatedUnitCost) { this.estimatedUnitCost = estimatedUnitCost; }

    Long getId() { return id; }
    Integer getLineNumber() { return lineNumber; }
    String getItemDescription() { return itemDescription; }
    BigDecimal getQuantity() { return quantity; }
    String getUnitOfMeasure() { return unitOfMeasure; }
    BigDecimal getEstimatedUnitCost() { return estimatedUnitCost; }
}
