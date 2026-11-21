package com.abes.defenceprocurement.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abes.defenceprocurement.user.AppRole;
import com.abes.defenceprocurement.user.AppUser;
import com.abes.defenceprocurement.user.AppUserRepository;
import com.abes.defenceprocurement.contract.ContractRepository;
import com.abes.defenceprocurement.contract.ContractStatusRepository;
import com.abes.defenceprocurement.contract.VendorRepository;
import com.abes.defenceprocurement.requisition.ProcurementRequisitionRepository;
import com.abes.defenceprocurement.requisition.RequisitionItemRepository;
import com.abes.defenceprocurement.requisition.RequisitionStatusRepository;
import com.abes.defenceprocurement.vendor.VendorEvaluationRepository;
import com.abes.defenceprocurement.invoice.InvoiceRepository;
import com.abes.defenceprocurement.invoice.InvoiceStatusRepository;
import com.abes.defenceprocurement.invoice.PaymentRepository;
import com.abes.defenceprocurement.invoice.PaymentStatusRepository;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration")
@AutoConfigureMockMvc
class AuthenticationHttpIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @MockBean private AppUserRepository users;
    @MockBean private ContractRepository contracts;
    @MockBean private VendorRepository vendors;
    @MockBean private ContractStatusRepository contractStatuses;
    @MockBean private ProcurementRequisitionRepository requisitions;
    @MockBean private RequisitionItemRepository requisitionItems;
    @MockBean private RequisitionStatusRepository requisitionStatuses;
    @MockBean private VendorEvaluationRepository vendorEvaluations;
    @MockBean private InvoiceRepository invoices;
    @MockBean private InvoiceStatusRepository invoiceStatuses;
    @MockBean private PaymentRepository payments;
    @MockBean private PaymentStatusRepository paymentStatuses;

    @BeforeEach
    void configureAccount() {
        AppRole role = mock(AppRole.class);
        when(role.getCode()).thenReturn("ROLE_SAFETY_OFFICER");
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn(12L);
        when(user.getUsername()).thenReturn("a.sharma");
        when(user.getDisplayName()).thenReturn("Aarav Sharma");
        when(user.getEmail()).thenReturn("aarav.sharma@example.invalid");
        when(user.getPasswordHash()).thenReturn(passwordEncoder.encode("integration-test-secret"));
        when(user.isActive()).thenReturn(true);
        when(user.getRoles()).thenReturn(Set.of(role));
        when(users.findByUsernameIgnoreCase("a.sharma")).thenReturn(Optional.of(user));
    }

    @Test
    void csrfLoginProfileAndLogoutWorkThroughTheSecurityFilterChain() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String csrf = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(csrfResult.getResponse().getContentAsString()).get("token").asText();
        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");

        MvcResult loginResult = mvc.perform(post("/api/auth/login")
                        .cookie(csrfCookie).header("X-XSRF-TOKEN", csrf)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"a.sharma\",\"password\":\"integration-test-secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("a.sharma"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_SAFETY_OFFICER"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Aarav Sharma"));

        mvc.perform(post("/api/auth/logout").session(session)
                        .cookie(csrfCookie).header("X-XSRF-TOKEN", csrf))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isUnauthorized());
    }

    @Test
    void badPasswordReturns401ThroughTheSecurityFilterChain() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/auth/csrf")).andReturn();
        String csrf = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(csrfResult.getResponse().getContentAsString()).get("token").asText();
        mvc.perform(post("/api/auth/login")
                        .cookie(csrfResult.getResponse().getCookie("XSRF-TOKEN"))
                        .header("X-XSRF-TOKEN", csrf)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"a.sharma\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void contractReadsRequireAuthentication() throws Exception {
        mvc.perform(get("/api/contracts")).andExpect(status().isUnauthorized());
    }

    @Test
    void requisitionReadsRequireAuthentication() throws Exception {
        mvc.perform(get("/api/requisitions")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/requisitions/options")).andExpect(status().isUnauthorized());
    }

    @Test
    void requisitionWritesRequireCsrfTokens() throws Exception {
        var authenticated = SecurityMockMvcRequestPostProcessors.user("requisition-user");
        mvc.perform(post("/api/requisitions").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(put("/api/requisitions/1").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(patch("/api/requisitions/1/status").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(post("/api/requisitions/1/items").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(put("/api/requisitions/1/items/1").with(authenticated)).andExpect(status().isForbidden());
    }

    @Test
    void contractWritesRequireCsrfTokens() throws Exception {
        var authenticated = SecurityMockMvcRequestPostProcessors.user("contract-user");
        mvc.perform(post("/api/contracts").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(put("/api/contracts/1").with(authenticated)).andExpect(status().isForbidden());
        mvc.perform(patch("/api/contracts/1/status").with(authenticated)).andExpect(status().isForbidden());
    }

    @Test
    void contractWriteMethodsPassCorsPreflightFromTheConfiguredFrontend() throws Exception {
        for (String method : Set.of("PUT", "PATCH")) {
            mvc.perform(options("/api/contracts/1")
                            .header("Origin", "http://localhost:4200")
                            .header("Access-Control-Request-Method", method)
                            .header("Access-Control-Request-Headers", "content-type,x-xsrf-token"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                    .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString(method)));
        }
    }
}
