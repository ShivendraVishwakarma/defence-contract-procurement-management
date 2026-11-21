package com.abes.defenceprocurement.invoice;

import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatus, Long> {
    Optional<InvoiceStatus> findByCode(String code);
    @Query("select new com.abes.defenceprocurement.invoice.InvoiceStatusOption(s.code, s.name) from InvoiceStatus s order by s.name")
    List<InvoiceStatusOption> findAllOptions();
}
