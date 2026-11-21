package com.abes.defenceprocurement.requisition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequisitionStatusRepository extends JpaRepository<RequisitionStatus, Long> {
    Optional<RequisitionStatus> findByCode(String code);

    @Query("select new com.abes.defenceprocurement.requisition.RequisitionStatusOption(s.code, s.name) " +
            "from RequisitionStatus s order by s.name")
    List<RequisitionStatusOption> findAllOptions();
}
