package com.abes.defenceprocurement.contract;

import java.util.List;
import java.net.URI;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    private final ContractService contracts;

    public ContractController(ContractService contracts) {
        this.contracts = contracts;
    }

    @GetMapping
    public List<ContractSummary> listContracts() {
        return contracts.listContracts();
    }

    @GetMapping("/options")
    public ContractOptions options() {
        return contracts.getOptions();
    }

    @GetMapping("/{id}")
    public ContractSummary getContract(@PathVariable Long id) {
        return contracts.getContract(id);
    }

    @PostMapping
    public ResponseEntity<ContractSummary> createContract(@Valid @RequestBody ContractCreateRequest request) {
        ContractSummary created = contracts.createContract(request);
        return ResponseEntity.created(URI.create("/api/contracts/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ContractSummary updateContract(@PathVariable Long id,
                                          @Valid @RequestBody ContractUpdateRequest request) {
        return contracts.updateContract(id, request);
    }

    @PatchMapping("/{id}/status")
    public ContractSummary changeStatus(@PathVariable Long id,
                                        @Valid @RequestBody ContractStatusRequest request) {
        return contracts.changeStatus(id, request);
    }
}
