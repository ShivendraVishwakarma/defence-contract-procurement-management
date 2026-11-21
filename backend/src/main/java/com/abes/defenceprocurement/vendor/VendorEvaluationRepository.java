package com.abes.defenceprocurement.vendor;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VendorEvaluationRepository extends JpaRepository<VendorEvaluation, Long> {
    @Query("""
        select new com.abes.defenceprocurement.vendor.VendorEvaluationSummary(
          e.id, r.id, r.requisitionNumber, v.id, v.code, v.name,
          u.id, u.username, e.score, e.recommendation, e.evaluatedAt)
        from VendorEvaluation e
        join e.requisition r
        join e.vendor v
        join e.evaluatedBy u
        order by e.evaluatedAt desc, e.id desc
        """)
    List<VendorEvaluationSummary> findAllSummaries();

    @Query("""
        select new com.abes.defenceprocurement.vendor.VendorEvaluationSummary(
          e.id, r.id, r.requisitionNumber, v.id, v.code, v.name,
          u.id, u.username, e.score, e.recommendation, e.evaluatedAt)
        from VendorEvaluation e
        join e.requisition r
        join e.vendor v
        join e.evaluatedBy u
        where e.id = :id
        """)
    Optional<VendorEvaluationSummary> findSummaryById(Long id);

    boolean existsByRequisitionIdAndVendorId(Long requisitionId, Long vendorId);
    boolean existsByRequisitionIdAndVendorIdAndIdNot(Long requisitionId, Long vendorId, Long id);
}
