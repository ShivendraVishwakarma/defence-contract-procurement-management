package com.abes.defenceprocurement.requisition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.abes.defenceprocurement.user.AppUserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequisitionServiceTest {
    @Mock ProcurementRequisitionRepository requisitions;
    @Mock RequisitionItemRepository items;
    @Mock RequisitionStatusRepository statuses;
    @Mock AppUserRepository users;
    RequisitionService service;
    RequisitionSummary summary;

    @BeforeEach void setUp() {
        service = new RequisitionService(requisitions, items, statuses, users);
        summary = new RequisitionSummary(7L, "J14-REQ-TEST-001", 3L, "Fictional Requester",
                "DRAFT", "Draft", "Fictional training supplies", "Fictional exercise", LocalDate.parse("2027-05-01"));
    }

    @Test void listsAndGetsRequisitionsWithItems() {
        when(requisitions.findAllForListing()).thenReturn(List.of(summary));
        assertEquals(List.of(summary), service.list());
        when(requisitions.findSummaryById(7L)).thenReturn(Optional.of(summary));
        when(items.findAllDtosByRequisitionId(7L)).thenReturn(List.of());
        assertEquals("J14-REQ-TEST-001", service.get(7L).requisitionNumber());
        assertTrue(service.get(7L).items().isEmpty());
    }

    @Test void createsRequisitionForAuthenticatedRequesterAndExistingStatus() {
        RequisitionCreateRequest request = new RequisitionCreateRequest("J14-REQ-TEST-001", "Training supplies",
                "Fictional exercise", LocalDate.parse("2027-05-01"), "DRAFT");
        RequisitionStatus status = mock(RequisitionStatus.class);
        when(requisitions.existsByRequisitionNumber(request.requisitionNumber())).thenReturn(false);
        when(statuses.findByCode("DRAFT")).thenReturn(Optional.of(status));
        ProcurementRequisition saved = mock(ProcurementRequisition.class);
        when(requisitions.saveAndFlush(any())).thenReturn(saved);
        when(saved.getId()).thenReturn(7L);
        when(requisitions.findSummaryById(7L)).thenReturn(Optional.of(summary));
        when(items.findAllDtosByRequisitionId(7L)).thenReturn(List.of());
        assertEquals(7L, service.create(request, 3L).id());
        verify(users).getReferenceById(3L);
    }

    @Test void rejectsDuplicateNumberAndUnknownStatus() {
        var request = new RequisitionCreateRequest("J14-REQ-TEST-001", "Title", "Reason", LocalDate.now(), "DRAFT");
        when(requisitions.existsByRequisitionNumber(request.requisitionNumber())).thenReturn(true);
        assertThrows(InvalidRequisitionRequestException.class, () -> service.create(request, 3L));
        when(requisitions.existsByRequisitionNumber(request.requisitionNumber())).thenReturn(false);
        when(statuses.findByCode("DRAFT")).thenReturn(Optional.empty());
        assertThrows(InvalidRequisitionRequestException.class, () -> service.create(request, 3L));
    }

    @Test void changesStatusUsingExistingStatusOnly() {
        ProcurementRequisition entity = mock(ProcurementRequisition.class);
        RequisitionStatus status = mock(RequisitionStatus.class);
        when(requisitions.findById(7L)).thenReturn(Optional.of(entity));
        when(statuses.findByCode("SUBMITTED")).thenReturn(Optional.of(status));
        when(requisitions.saveAndFlush(entity)).thenReturn(entity);
        when(entity.getId()).thenReturn(7L);
        when(requisitions.findSummaryById(7L)).thenReturn(Optional.of(summary));
        when(items.findAllDtosByRequisitionId(7L)).thenReturn(List.of());
        service.changeStatus(7L, new RequisitionStatusRequest("SUBMITTED"));
        verify(entity).setStatus(status);
        when(statuses.findByCode("MADE_UP")).thenReturn(Optional.empty());
        assertThrows(InvalidRequisitionRequestException.class,
                () -> service.changeStatus(7L, new RequisitionStatusRequest("MADE_UP")));
    }

    @Test void addsAndUpdatesItemsAndRejectsDuplicateLine() {
        ProcurementRequisition requisition = mock(ProcurementRequisition.class);
        RequisitionItem item = mock(RequisitionItem.class);
        when(item.getId()).thenReturn(9L);
        when(requisitions.findById(7L)).thenReturn(Optional.of(requisition));
        when(items.existsByRequisitionIdAndLineNumber(7L, 1)).thenReturn(false);
        when(items.saveAndFlush(any())).thenReturn(item);
        when(requisitions.findSummaryById(7L)).thenReturn(Optional.of(summary));
        when(items.findAllDtosByRequisitionId(7L)).thenReturn(List.of());
        var request = new RequisitionItemRequest(1, "Fictional radio battery", new BigDecimal("2.000"), "each", new BigDecimal("12.50"));
        service.addItem(7L, request);
        verify(items).saveAndFlush(any(RequisitionItem.class));
        when(items.findItemByIdAndRequisitionId(9L, 7L)).thenReturn(Optional.of(item));
        when(items.findItemByRequisitionIdAndLineNumber(7L, 1)).thenReturn(Optional.of(item));
        var updateRequest = new RequisitionItemRequest(1, "Fictional radio batteries updated for verification",
                new BigDecimal("3"), "each", new BigDecimal("12.5"));
        service.updateItem(7L, 9L, updateRequest);
        verify(item).setItemDescription("Fictional radio batteries updated for verification");
        verify(item).setQuantity(new BigDecimal("3"));
        verify(item).setEstimatedUnitCost(new BigDecimal("12.5"));
        RequisitionItem otherItem = mock(RequisitionItem.class);
        when(otherItem.getId()).thenReturn(10L);
        when(items.findItemByRequisitionIdAndLineNumber(7L, 1)).thenReturn(Optional.of(otherItem));
        assertThrows(InvalidRequisitionRequestException.class, () -> service.updateItem(7L, 9L, updateRequest));
    }

    @Test void returnsNotFoundForMissingRequisitionOrItem() {
        when(requisitions.findSummaryById(99L)).thenReturn(Optional.empty());
        assertThrows(RequisitionNotFoundException.class, () -> service.get(99L));
        when(requisitions.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RequisitionNotFoundException.class,
                () -> service.update(99L, new RequisitionUpdateRequest("R", "T", "J", LocalDate.now())));
        when(requisitions.findById(7L)).thenReturn(Optional.of(mock(ProcurementRequisition.class)));
        when(items.findItemByIdAndRequisitionId(404L, 7L)).thenReturn(Optional.empty());
        assertThrows(RequisitionItemNotFoundException.class,
                () -> service.updateItem(7L, 404L, new RequisitionItemRequest(1, "Item", BigDecimal.ONE, "each", BigDecimal.ZERO)));
    }
}
