package com.abes.defenceprocurement.requisition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long> {
    @Query("select new com.abes.defenceprocurement.requisition.RequisitionItemDto(" +
            "i.id, i.lineNumber, i.itemDescription, i.quantity, i.unitOfMeasure, i.estimatedUnitCost) " +
            "from RequisitionItem i where i.requisition.id = :requisitionId order by i.lineNumber")
    List<RequisitionItemDto> findAllDtosByRequisitionId(@Param("requisitionId") Long requisitionId);

    @Query("select i from RequisitionItem i where i.id = :itemId and i.requisition.id = :requisitionId")
    Optional<RequisitionItem> findItemByIdAndRequisitionId(@Param("itemId") Long itemId,
                                                          @Param("requisitionId") Long requisitionId);

    @Query("select i from RequisitionItem i " +
            "where i.requisition.id = :requisitionId and i.lineNumber = :lineNumber")
    Optional<RequisitionItem> findItemByRequisitionIdAndLineNumber(@Param("requisitionId") Long requisitionId,
                                                                    @Param("lineNumber") Integer lineNumber);

    @Query("select case when count(i) > 0 then true else false end from RequisitionItem i " +
            "where i.requisition.id = :requisitionId and i.lineNumber = :lineNumber")
    boolean existsByRequisitionIdAndLineNumber(@Param("requisitionId") Long requisitionId,
                                               @Param("lineNumber") Integer lineNumber);
}
