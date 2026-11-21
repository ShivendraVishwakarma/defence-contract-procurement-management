package com.abes.defenceprocurement.invoice;

import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("""
      select new com.abes.defenceprocurement.invoice.InvoiceSummary(i.id, i.invoiceNumber, c.id, c.contractNumber,
      s.code, s.name, i.invoiceDate, i.dueDate, i.totalAmount)
      from Invoice i join i.contract c join i.status s order by i.dueDate, i.invoiceNumber
      """)
    List<InvoiceSummary> findAllSummaries();
    @Query("""
      select new com.abes.defenceprocurement.invoice.InvoiceSummary(i.id, i.invoiceNumber, c.id, c.contractNumber,
      s.code, s.name, i.invoiceDate, i.dueDate, i.totalAmount)
      from Invoice i join i.contract c join i.status s where i.id = :id
      """)
    Optional<InvoiceSummary> findSummaryById(Long id);
    boolean existsByInvoiceNumber(String invoiceNumber);
}
