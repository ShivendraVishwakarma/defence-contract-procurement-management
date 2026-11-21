package com.abes.defenceprocurement.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {
    @Mock private ContractRepository contracts;
    @Mock private VendorRepository vendors;
    @Mock private ContractStatusRepository statuses;
    @InjectMocks private ContractService service;

    private Vendor vendor;
    private ContractStatus draft;
    private ContractStatus active;

    @BeforeEach
    void setUp() {
        vendor = mock(Vendor.class);
        lenient().when(vendor.getName()).thenReturn("Fictional vendor");
        draft = status("DRAFT", "Draft");
        active = status("ACTIVE", "Active");
    }

    @Test
    void listsSummariesFromTheRepository() {
        ContractSummary summary = summary(1L, "DRAFT", "Draft");
        when(contracts.findAllForListing()).thenReturn(List.of(summary));

        assertEquals(List.of(summary), service.listContracts());
        verify(contracts).findAllForListing();
    }

    @Test
    void createsAContractUsingExistingVendorAndStatus() {
        when(contracts.existsByContractNumber("J14-CN-2026-010")).thenReturn(false);
        when(vendors.findById(5L)).thenReturn(Optional.of(vendor));
        when(statuses.findByCode("DRAFT")).thenReturn(Optional.of(draft));
        when(contracts.saveAndFlush(any(Contract.class))).thenAnswer(invocation -> {
            Contract saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ContractSummary result = service.createContract(createRequest());

        assertEquals(10L, result.id());
        assertEquals("Fictional vendor", result.vendorName());
        assertEquals("DRAFT", result.statusCode());
    }

    @Test
    void updatesDetailsWithoutChangingTheCurrentStatus() {
        Contract existing = entity(11L, draft);
        when(contracts.findById(11L)).thenReturn(Optional.of(existing));
        when(contracts.existsByContractNumberAndIdNot("J14-CN-2026-011", 11L)).thenReturn(false);
        when(vendors.findById(5L)).thenReturn(Optional.of(vendor));
        when(contracts.saveAndFlush(existing)).thenReturn(existing);

        ContractSummary result = service.updateContract(11L, updateRequest());

        assertEquals("DRAFT", result.statusCode());
        assertEquals("Updated title", result.title());
    }

    @Test
    void changesStatusUsingAnExistingStatus() {
        Contract existing = entity(12L, draft);
        when(contracts.findById(12L)).thenReturn(Optional.of(existing));
        when(statuses.findByCode("ACTIVE")).thenReturn(Optional.of(active));
        when(contracts.saveAndFlush(existing)).thenReturn(existing);

        ContractSummary result = service.changeStatus(12L, new ContractStatusRequest("ACTIVE"));

        assertEquals("ACTIVE", result.statusCode());
    }

    @Test
    void rejectsUnknownVendorAndMissingContract() {
        when(contracts.existsByContractNumber("J14-CN-2026-010")).thenReturn(false);
        when(vendors.findById(5L)).thenReturn(Optional.empty());
        assertThrows(InvalidContractRequestException.class, () -> service.createContract(createRequest()));
        assertThrows(ContractNotFoundException.class, () -> service.getContract(999L));
    }

    private Contract entity(Long id, ContractStatus status) {
        Contract contract = new Contract();
        contract.setId(id);
        contract.setContractNumber("J14-CN-2026-" + id);
        contract.setTitle("Original title");
        contract.setVendor(vendor);
        contract.setStatus(status);
        contract.setStartDate(LocalDate.parse("2026-01-01"));
        contract.setEndDate(LocalDate.parse("2026-12-31"));
        contract.setContractValue(new BigDecimal("1500.00"));
        contract.setSummary("Existing summary");
        return contract;
    }

    private ContractStatus status(String code, String name) {
        ContractStatus status = mock(ContractStatus.class);
        lenient().when(status.getCode()).thenReturn(code);
        lenient().when(status.getName()).thenReturn(name);
        return status;
    }

    private ContractCreateRequest createRequest() {
        return new ContractCreateRequest("J14-CN-2026-010", "New title", 5L, "DRAFT",
                LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31"),
                new BigDecimal("1500.00"), "New summary");
    }

    private ContractUpdateRequest updateRequest() {
        return new ContractUpdateRequest("J14-CN-2026-011", "Updated title", 5L,
                LocalDate.parse("2026-02-01"), LocalDate.parse("2026-11-30"),
                new BigDecimal("2500.00"), "Updated summary");
    }

    private ContractSummary summary(Long id, String statusCode, String statusName) {
        return new ContractSummary(id, "J14-CN-2026-010", "Title", 5L, "Fictional vendor",
                statusCode, statusName, LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-12-31"), new BigDecimal("1500.00"), "Summary");
    }
}
