package com.abes.defenceprocurement.contract;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ContractControllerTest {
    private ContractService service;
    private MockMvc mvc;

    private final ContractSummary summary = new ContractSummary(7L, "J14-CN-2025-004", "Fictional support", 5L,
            "Fictional vendor", "ACTIVE", "Active", LocalDate.parse("2025-06-15"),
            LocalDate.parse("2026-06-14"), new BigDecimal("740000.00"), "Training record");

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(ContractService.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new ContractController(service))
                .setControllerAdvice(new ContractExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listsAndReadsContractSummaries() throws Exception {
        when(service.listContracts()).thenReturn(List.of(summary));
        when(service.getContract(7L)).thenReturn(summary);

        mvc.perform(get("/api/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contractNumber").value("J14-CN-2025-004"))
                .andExpect(jsonPath("$[0].vendorName").value("Fictional vendor"));
        mvc.perform(get("/api/contracts/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("Active"));
    }

    @Test
    void createsContractWithCreatedStatusAndLocation() throws Exception {
        when(service.createContract(org.mockito.ArgumentMatchers.any(ContractCreateRequest.class))).thenReturn(summary);

        mvc.perform(post("/api/contracts").contentType("application/json").content(createJson()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/contracts/7"))
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void updatesDetailsAndChangesStatus() throws Exception {
        when(service.updateContract(org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.any(ContractUpdateRequest.class))).thenReturn(summary);
        when(service.changeStatus(org.mockito.ArgumentMatchers.eq(7L),
                org.mockito.ArgumentMatchers.any(ContractStatusRequest.class))).thenReturn(summary);

        mvc.perform(put("/api/contracts/7").contentType("application/json").content(updateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contractNumber").value("J14-CN-2025-004"));
        mvc.perform(patch("/api/contracts/7/status").contentType("application/json")
                        .content("{\"statusCode\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("ACTIVE"));
    }

    @Test
    void rejectsInvalidFieldsAndDateRanges() throws Exception {
        mvc.perform(post("/api/contracts").contentType("application/json")
                        .content("{\"contractNumber\":\"\",\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Contract request is invalid"));
        mvc.perform(post("/api/contracts").contentType("application/json")
                        .content(createJson().replace("2026-06-14", "2024-06-14")))
                .andExpect(status().isBadRequest());
        verify(service, never()).createContract(org.mockito.ArgumentMatchers.any(ContractCreateRequest.class));
    }

    @Test
    void returnsNotFoundForMissingContract() throws Exception {
        when(service.getContract(999L)).thenThrow(new ContractNotFoundException(999L));

        mvc.perform(get("/api/contracts/999")).andExpect(status().isNotFound());
    }

    private String createJson() {
        return """
                {"contractNumber":"J14-CN-2025-004","title":"Fictional support","vendorId":5,
                 "statusCode":"DRAFT","startDate":"2025-06-15","endDate":"2026-06-14",
                 "contractValue":740000.00,"summary":"Training record"}
                """;
    }

    private String updateJson() {
        return """
                {"contractNumber":"J14-CN-2025-004","title":"Updated support","vendorId":5,
                 "startDate":"2025-06-15","endDate":"2026-06-14",
                 "contractValue":740000.00,"summary":"Updated training record"}
                """;
    }
}
