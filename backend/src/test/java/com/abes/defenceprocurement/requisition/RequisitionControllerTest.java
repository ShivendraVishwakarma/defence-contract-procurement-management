package com.abes.defenceprocurement.requisition;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RequisitionController.class)
@AutoConfigureMockMvc
@Import(RequisitionExceptionHandler.class)
class RequisitionControllerTest {
    @Autowired MockMvc mvc;
    @MockBean RequisitionService service;

    private RequisitionDetail detail;
    private AppUserPrincipal principal;

    @BeforeEach void setUp() {
        detail = new RequisitionDetail(17L, "J14-REQ-HTTP-TEST", 4L, "Fictional Requester",
                "DRAFT", "Draft", "Fictional field kit", "Fictional training exercise",
                LocalDate.parse("2027-07-01"), List.of());
        principal = new AppUserPrincipal(4L, "fictional.user", "Fictional Requester",
                "fictional@example.invalid", "unused-test-hash", true, List.of());
    }

    @Test void listAndDetailReturnDtoResponses() throws Exception {
        when(service.list()).thenReturn(List.of(new RequisitionSummary(detail.id(), detail.requisitionNumber(),
                detail.requesterId(), detail.requesterName(), detail.statusCode(), detail.statusName(),
                detail.title(), detail.justification(), detail.neededByDate())));
        when(service.get(17L)).thenReturn(detail);
        mvc.perform(get("/api/requisitions").with(user(principal))).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requisitionNumber").value("J14-REQ-HTTP-TEST"));
        mvc.perform(get("/api/requisitions/17").with(user(principal))).andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test void createReturns201AndUsesAuthenticatedRequester() throws Exception {
        when(service.create(any(RequisitionCreateRequest.class), eq(4L))).thenReturn(detail);
        mvc.perform(post("/api/requisitions").with(user(principal)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"requisitionNumber":"J14-REQ-HTTP-TEST","title":"Fictional field kit",
                                 "justification":"Fictional training exercise","neededByDate":"2027-07-01","statusCode":"DRAFT"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/requisitions/17"));
        verify(service).create(any(RequisitionCreateRequest.class), eq(4L));
    }

    @Test void invalidPayloadReturns400AndMissingRecordsReturn404() throws Exception {
        mvc.perform(post("/api/requisitions").with(user(principal)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requisitionNumber\":\"\",\"title\":\"\",\"justification\":\"\",\"neededByDate\":\"not-a-date\",\"statusCode\":\"\"}"))
                .andExpect(status().isBadRequest());
        when(service.get(404L)).thenThrow(new RequisitionNotFoundException(404L));
        mvc.perform(get("/api/requisitions/404").with(user(principal))).andExpect(status().isNotFound());
    }

    @Test void itemAndStatusRoutesDelegateToService() throws Exception {
        when(service.changeStatus(eq(17L), any(RequisitionStatusRequest.class))).thenReturn(detail);
        when(service.addItem(eq(17L), any(RequisitionItemRequest.class))).thenReturn(detail);
        when(service.updateItem(eq(17L), eq(2L), any(RequisitionItemRequest.class))).thenReturn(detail);
        mvc.perform(patch("/api/requisitions/17/status").with(user(principal)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusCode\":\"DRAFT\"}"))
                .andExpect(status().isOk());
        String item = "{\"lineNumber\":1,\"itemDescription\":\"Fictional batteries\",\"quantity\":2.000,\"unitOfMeasure\":\"each\",\"estimatedUnitCost\":12.50}";
        mvc.perform(post("/api/requisitions/17/items").with(user(principal)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(item))
                .andExpect(status().isOk());
        String itemUpdate = "{\"lineNumber\":1,\"itemDescription\":\"Fictional radio batteries updated for verification\",\"quantity\":3,\"unitOfMeasure\":\"each\",\"estimatedUnitCost\":12.5}";
        mvc.perform(put("/api/requisitions/17/items/2").with(user(principal)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(itemUpdate))
                .andExpect(status().isOk());
        verify(service).updateItem(eq(17L), eq(2L), argThat(request ->
                request.lineNumber() == 1
                        && request.itemDescription().equals("Fictional radio batteries updated for verification")
                        && request.quantity().compareTo(new java.math.BigDecimal("3")) == 0
                        && request.unitOfMeasure().equals("each")
                        && request.estimatedUnitCost().compareTo(new java.math.BigDecimal("12.5")) == 0));
    }
}
