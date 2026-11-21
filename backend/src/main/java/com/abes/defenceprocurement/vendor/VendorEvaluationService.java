package com.abes.defenceprocurement.vendor;

import com.abes.defenceprocurement.contract.Vendor;
import com.abes.defenceprocurement.contract.VendorRepository;
import com.abes.defenceprocurement.requisition.ProcurementRequisition;
import com.abes.defenceprocurement.requisition.ProcurementRequisitionRepository;
import com.abes.defenceprocurement.user.AppUserRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendorEvaluationService {
    private final VendorEvaluationRepository evaluations;
    private final VendorRepository vendors;
    private final ProcurementRequisitionRepository requisitions;
    private final AppUserRepository users;

    public VendorEvaluationService(VendorEvaluationRepository evaluations, VendorRepository vendors,
                                   ProcurementRequisitionRepository requisitions, AppUserRepository users) {
        this.evaluations = evaluations;
        this.vendors = vendors;
        this.requisitions = requisitions;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<VendorEvaluationSummary> list() { return evaluations.findAllSummaries(); }

    @Transactional(readOnly = true)
    public VendorEvaluationOptions options() {
        return new VendorEvaluationOptions(vendors.findAllOptions(), requisitions.findAllForListing());
    }

    @Transactional
    public VendorEvaluationSummary create(VendorEvaluationRequest request, Long userId) {
        if (evaluations.existsByRequisitionIdAndVendorId(request.requisitionId(), request.vendorId())) {
            throw new InvalidVendorEvaluationException("This vendor is already evaluated for the requisition");
        }
        VendorEvaluation evaluation = new VendorEvaluation();
        apply(evaluation, request);
        evaluation.setEvaluatedBy(users.getReferenceById(userId));
        return saveAndRead(evaluation);
    }

    @Transactional
    public VendorEvaluationSummary update(Long id, VendorEvaluationRequest request) {
        VendorEvaluation evaluation = evaluations.findById(id)
                .orElseThrow(() -> new VendorEvaluationNotFoundException(id));
        if (evaluations.existsByRequisitionIdAndVendorIdAndIdNot(request.requisitionId(), request.vendorId(), id)) {
            throw new InvalidVendorEvaluationException("This vendor is already evaluated for the requisition");
        }
        apply(evaluation, request);
        return saveAndRead(evaluation);
    }

    private void apply(VendorEvaluation evaluation, VendorEvaluationRequest request) {
        ProcurementRequisition requisition = requisitions.findById(request.requisitionId())
                .orElseThrow(() -> new InvalidVendorEvaluationException("Requisition does not exist"));
        Vendor vendor = vendors.findById(request.vendorId())
                .orElseThrow(() -> new InvalidVendorEvaluationException("Vendor does not exist"));
        evaluation.setRequisition(requisition);
        evaluation.setVendor(vendor);
        evaluation.setScore(request.score());
        evaluation.setRecommendation(request.recommendation().trim());
    }

    private VendorEvaluationSummary saveAndRead(VendorEvaluation evaluation) {
        try {
            Long id = evaluations.saveAndFlush(evaluation).getId();
            return evaluations.findSummaryById(id).orElseThrow(() -> new VendorEvaluationNotFoundException(id));
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidVendorEvaluationException("Vendor evaluation conflicts with existing data");
        }
    }
}
