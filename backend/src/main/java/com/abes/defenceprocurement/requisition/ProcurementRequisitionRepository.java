package com.abes.defenceprocurement.requisition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface ProcurementRequisitionRepository extends JpaRepository<ProcurementRequisition, Long> {
    boolean existsByRequisitionNumber(String requisitionNumber);

    boolean existsByRequisitionNumberAndIdNot(String requisitionNumber, Long id);

    @Query("""
            select new com.abes.defenceprocurement.requisition.RequisitionSummary(
                r.id, r.requisitionNumber, u.id, u.displayName, s.code, s.name,
                r.title, r.justification, r.neededByDate)
            from ProcurementRequisition r
            join r.requestedBy u
            join r.status s
            order by r.requisitionNumber
            """)
    List<RequisitionSummary> findAllForListing();

    @Query("""
            select new com.abes.defenceprocurement.requisition.RequisitionSummary(
                r.id, r.requisitionNumber, u.id, u.displayName, s.code, s.name,
                r.title, r.justification, r.neededByDate)
            from ProcurementRequisition r
            join r.requestedBy u
            join r.status s
            where r.id = :id
            """)
    Optional<RequisitionSummary> findSummaryById(@Param("id") Long id);
}
