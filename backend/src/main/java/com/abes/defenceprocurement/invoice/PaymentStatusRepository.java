package com.abes.defenceprocurement.invoice;

import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    Optional<PaymentStatus> findByCode(String code);
    @Query("select new com.abes.defenceprocurement.invoice.PaymentStatusOption(s.code, s.name) from PaymentStatus s order by s.name")
    List<PaymentStatusOption> findAllOptions();
}
