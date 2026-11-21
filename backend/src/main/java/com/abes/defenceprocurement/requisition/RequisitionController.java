package com.abes.defenceprocurement.requisition;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requisitions")
public class RequisitionController {
    private final RequisitionService service;
    public RequisitionController(RequisitionService service) { this.service = service; }

    @GetMapping public List<RequisitionSummary> list() { return service.list(); }
    @GetMapping("/options") public RequisitionOptions options() { return service.options(); }
    @GetMapping("/{id}") public RequisitionDetail get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<RequisitionDetail> create(@Valid @RequestBody RequisitionCreateRequest request,
                                                    @AuthenticationPrincipal AppUserPrincipal principal) {
        RequisitionDetail created = service.create(request, principal.id());
        return ResponseEntity.created(URI.create("/api/requisitions/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public RequisitionDetail update(@PathVariable Long id, @Valid @RequestBody RequisitionUpdateRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public RequisitionDetail changeStatus(@PathVariable Long id, @Valid @RequestBody RequisitionStatusRequest request) {
        return service.changeStatus(id, request);
    }

    @PostMapping("/{id}/items")
    public RequisitionDetail addItem(@PathVariable Long id, @Valid @RequestBody RequisitionItemRequest request) {
        return service.addItem(id, request);
    }

    @PutMapping("/{id}/items/{itemId}")
    public RequisitionDetail updateItem(@PathVariable Long id, @PathVariable Long itemId,
                                        @Valid @RequestBody RequisitionItemRequest request) {
        return service.updateItem(id, itemId, request);
    }
}
