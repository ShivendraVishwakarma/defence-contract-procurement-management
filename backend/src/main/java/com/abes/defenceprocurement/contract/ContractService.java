package com.abes.defenceprocurement.contract;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {
    private final ContractRepository contracts;
    private final VendorRepository vendors;
    private final ContractStatusRepository statuses;

    public ContractService(ContractRepository contracts, VendorRepository vendors,
                           ContractStatusRepository statuses) {
        this.contracts = contracts;
        this.vendors = vendors;
        this.statuses = statuses;
    }

    @Transactional(readOnly = true)
    public List<ContractSummary> listContracts() {
        return contracts.findAllForListing();
    }

    @Transactional(readOnly = true)
    public ContractSummary getContract(Long id) {
        return contracts.findSummaryById(id).orElseThrow(() -> new ContractNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ContractOptions getOptions() {
        return new ContractOptions(vendors.findAllOptions(), statuses.findAllOptions());
    }

    @Transactional
    public ContractSummary createContract(ContractCreateRequest request) {
        String contractNumber = request.contractNumber().trim();
        if (contracts.existsByContractNumber(contractNumber)) {
            throw new InvalidContractRequestException("Contract number already exists");
        }

        Contract contract = new Contract();
        contract.setContractNumber(contractNumber);
        contract.setTitle(request.title().trim());
        contract.setVendor(requireVendor(request.vendorId()));
        contract.setStatus(requireStatus(request.statusCode()));
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setContractValue(request.contractValue());
        contract.setSummary(request.summary().trim());
        return toSummary(save(contract));
    }

    @Transactional
    public ContractSummary updateContract(Long id, ContractUpdateRequest request) {
        Contract contract = contracts.findById(id).orElseThrow(() -> new ContractNotFoundException(id));
        String contractNumber = request.contractNumber().trim();
        if (contracts.existsByContractNumberAndIdNot(contractNumber, id)) {
            throw new InvalidContractRequestException("Contract number already exists");
        }

        contract.setContractNumber(contractNumber);
        contract.setTitle(request.title().trim());
        contract.setVendor(requireVendor(request.vendorId()));
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setContractValue(request.contractValue());
        contract.setSummary(request.summary().trim());
        return toSummary(save(contract));
    }

    @Transactional
    public ContractSummary changeStatus(Long id, ContractStatusRequest request) {
        Contract contract = contracts.findById(id).orElseThrow(() -> new ContractNotFoundException(id));
        contract.setStatus(requireStatus(request.statusCode()));
        return toSummary(save(contract));
    }

    private Vendor requireVendor(Long id) {
        return vendors.findById(id)
                .orElseThrow(() -> new InvalidContractRequestException("Vendor does not exist"));
    }

    private ContractStatus requireStatus(String code) {
        return statuses.findByCode(code.trim())
                .orElseThrow(() -> new InvalidContractRequestException("Contract status does not exist"));
    }

    private Contract save(Contract contract) {
        try {
            return contracts.saveAndFlush(contract);
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidContractRequestException("Contract conflicts with existing repository data");
        }
    }

    private ContractSummary toSummary(Contract contract) {
        return new ContractSummary(contract.getId(), contract.getContractNumber(), contract.getTitle(),
                contract.getVendor().getId(), contract.getVendor().getName(),
                contract.getStatus().getCode(), contract.getStatus().getName(),
                contract.getStartDate(), contract.getEndDate(), contract.getContractValue(), contract.getSummary());
    }
}
