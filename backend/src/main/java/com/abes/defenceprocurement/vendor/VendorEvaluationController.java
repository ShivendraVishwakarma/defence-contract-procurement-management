package com.abes.defenceprocurement.vendor;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendor-evaluations")
public class VendorEvaluationController {
    private final VendorEvaluationService service;

    public VendorEvaluationController(VendorEvaluationService service) { this.service = service; }

    @GetMapping
    public List<VendorEvaluationSummary> list() { return service.list(); }

    @GetMapping("/options")
    public VendorEvaluationOptions options() { return service.options(); }

    @PostMapping
    public ResponseEntity<VendorEvaluationSummary> create(
            @Valid @RequestBody VendorEvaluationRequest request,
            @AuthenticationPrincipal AppUserPrincipal principal) {
        VendorEvaluationSummary created = service.create(request, principal.id());
        return ResponseEntity.created(URI.create("/api/vendor-evaluations/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public VendorEvaluationSummary update(@PathVariable Long id,
                                          @Valid @RequestBody VendorEvaluationRequest request) {
        return service.update(id, request);
    }
}
