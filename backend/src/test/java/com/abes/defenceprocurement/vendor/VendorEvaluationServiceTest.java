package com.abes.defenceprocurement.vendor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.abes.defenceprocurement.contract.Vendor;
import com.abes.defenceprocurement.contract.VendorRepository;
import com.abes.defenceprocurement.requisition.ProcurementRequisition;
import com.abes.defenceprocurement.requisition.ProcurementRequisitionRepository;
import com.abes.defenceprocurement.user.AppUser;
import com.abes.defenceprocurement.user.AppUserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VendorEvaluationServiceTest {
    private final VendorEvaluationRepository evaluations = mock(VendorEvaluationRepository.class);
    private final VendorRepository vendors = mock(VendorRepository.class);
    private final ProcurementRequisitionRepository requisitions = mock(ProcurementRequisitionRepository.class);
    private final AppUserRepository users = mock(AppUserRepository.class);
    private final VendorEvaluationService service = new VendorEvaluationService(evaluations, vendors, requisitions, users);

    @Test
    void rejectsDuplicateVendorForRequisition() {
        when(evaluations.existsByRequisitionIdAndVendorId(4L, 7L)).thenReturn(true);
        assertThrows(InvalidVendorEvaluationException.class,
                () -> service.create(new VendorEvaluationRequest(4L, 7L, BigDecimal.TEN, "Use"), 9L));
        verifyNoInteractions(vendors, requisitions, users);
    }

    @Test
    void createsEvaluationForAuthenticatedUser() {
        when(evaluations.existsByRequisitionIdAndVendorId(4L, 7L)).thenReturn(false);
        when(requisitions.findById(4L)).thenReturn(Optional.of(mock(ProcurementRequisition.class)));
        when(vendors.findById(7L)).thenReturn(Optional.of(mock(Vendor.class)));
        when(users.getReferenceById(9L)).thenReturn(mock(AppUser.class));
        when(evaluations.saveAndFlush(any(VendorEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(evaluations.findSummaryById(any())).thenReturn(Optional.of(mock(VendorEvaluationSummary.class)));

        service.create(new VendorEvaluationRequest(4L, 7L, BigDecimal.valueOf(92), "Recommended"), 9L);

        verify(evaluations).saveAndFlush(any(VendorEvaluation.class));
    }
}
