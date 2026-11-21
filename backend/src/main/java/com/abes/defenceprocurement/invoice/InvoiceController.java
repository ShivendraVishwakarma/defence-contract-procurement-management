package com.abes.defenceprocurement.invoice;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService service;
    public InvoiceController(InvoiceService service) { this.service = service; }
    @GetMapping public List<InvoiceSummary> list() { return service.list(); }
    @GetMapping("/options") public InvoiceOptions options() { return service.options(); }
    @PostMapping public ResponseEntity<InvoiceSummary> create(@Valid @RequestBody InvoiceRequest request) {
        InvoiceSummary created = service.create(request);
        return ResponseEntity.created(URI.create("/api/invoices/" + created.id())).body(created);
    }
    @PatchMapping("/{id}/status") public InvoiceSummary status(@PathVariable Long id, @Valid @RequestBody InvoiceStatusRequest request) {
        return service.changeStatus(id, request);
    }
    @PostMapping("/{id}/payments") public ResponseEntity<Void> payment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        service.addPayment(id, request); return ResponseEntity.noContent().build();
    }
}
