package com.abes.defenceprocurement.requisition;

import com.abes.defenceprocurement.user.AppUserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequisitionService {
    private static final Logger logger = LoggerFactory.getLogger(RequisitionService.class);

    private final ProcurementRequisitionRepository requisitions;
    private final RequisitionItemRepository items;
    private final RequisitionStatusRepository statuses;
    private final AppUserRepository users;

    public RequisitionService(ProcurementRequisitionRepository requisitions, RequisitionItemRepository items,
                              RequisitionStatusRepository statuses, AppUserRepository users) {
        this.requisitions = requisitions;
        this.items = items;
        this.statuses = statuses;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<RequisitionSummary> list() { return requisitions.findAllForListing(); }

    @Transactional(readOnly = true)
    public RequisitionDetail get(Long id) {
        RequisitionSummary summary = requisitions.findSummaryById(id)
                .orElseThrow(() -> new RequisitionNotFoundException(id));
        return detail(summary);
    }

    @Transactional(readOnly = true)
    public RequisitionOptions options() { return new RequisitionOptions(statuses.findAllOptions()); }

    @Transactional
    public RequisitionDetail create(RequisitionCreateRequest request, Long requesterId) {
        String number = request.requisitionNumber().trim();
        if (requisitions.existsByRequisitionNumber(number)) {
            throw new InvalidRequisitionRequestException("Requisition number already exists");
        }
        ProcurementRequisition requisition = new ProcurementRequisition();
        requisition.setRequisitionNumber(number);
        requisition.setTitle(request.title().trim());
        requisition.setJustification(request.justification().trim());
        requisition.setNeededByDate(request.neededByDate());
        requisition.setRequestedBy(users.getReferenceById(requesterId));
        requisition.setStatus(requireStatus(request.statusCode()));
        return savedDetail(save(requisition));
    }

    @Transactional
    public RequisitionDetail update(Long id, RequisitionUpdateRequest request) {
        ProcurementRequisition requisition = find(id);
        String number = request.requisitionNumber().trim();
        if (requisitions.existsByRequisitionNumberAndIdNot(number, id)) {
            throw new InvalidRequisitionRequestException("Requisition number already exists");
        }
        requisition.setRequisitionNumber(number);
        requisition.setTitle(request.title().trim());
        requisition.setJustification(request.justification().trim());
        requisition.setNeededByDate(request.neededByDate());
        return savedDetail(save(requisition));
    }

    @Transactional
    public RequisitionDetail changeStatus(Long id, RequisitionStatusRequest request) {
        ProcurementRequisition requisition = find(id);
        requisition.setStatus(requireStatus(request.statusCode()));
        return savedDetail(save(requisition));
    }

    @Transactional
    public RequisitionDetail addItem(Long id, RequisitionItemRequest request) {
        ProcurementRequisition requisition = find(id);
        if (items.existsByRequisitionIdAndLineNumber(id, request.lineNumber())) {
            throw new InvalidRequisitionRequestException("Line number already exists on this requisition");
        }
        RequisitionItem item = new RequisitionItem();
        item.setRequisition(requisition);
        apply(item, request);
        saveItem(item);
        return get(id);
    }

    @Transactional
    public RequisitionDetail updateItem(Long id, Long itemId, RequisitionItemRequest request) {
        find(id);
        RequisitionItem item = items.findItemByIdAndRequisitionId(itemId, id)
                .orElseThrow(() -> new RequisitionItemNotFoundException(itemId));
        RequisitionItem lineItem = items.findItemByRequisitionIdAndLineNumber(id, request.lineNumber()).orElse(null);
        if (lineItem != null && !itemId.equals(lineItem.getId())) {
            throw new InvalidRequisitionRequestException("Line number already exists on this requisition");
        }
        apply(item, request);
        saveItem(item);
        return get(id);
    }

    private ProcurementRequisition find(Long id) {
        return requisitions.findById(id).orElseThrow(() -> new RequisitionNotFoundException(id));
    }

    private RequisitionStatus requireStatus(String code) {
        return statuses.findByCode(code.trim())
                .orElseThrow(() -> new InvalidRequisitionRequestException("Requisition status does not exist"));
    }

    private void apply(RequisitionItem item, RequisitionItemRequest request) {
        item.setLineNumber(request.lineNumber());
        item.setItemDescription(request.itemDescription().trim());
        item.setQuantity(request.quantity());
        item.setUnitOfMeasure(request.unitOfMeasure().trim());
        item.setEstimatedUnitCost(request.estimatedUnitCost());
    }

    private ProcurementRequisition save(ProcurementRequisition requisition) {
        try { return requisitions.saveAndFlush(requisition); }
        catch (DataIntegrityViolationException exception) {
            throw new InvalidRequisitionRequestException("Requisition conflicts with existing repository data");
        }
    }

    private void saveItem(RequisitionItem item) {
        try { items.saveAndFlush(item); }
        catch (DataIntegrityViolationException exception) {
            logger.warn("Requisition item persistence failed for item {} line {}", item.getId(),
                    item.getLineNumber(), exception);
            throw new InvalidRequisitionRequestException("Requisition item conflicts with existing repository data");
        }
    }

    private RequisitionDetail savedDetail(ProcurementRequisition requisition) {
        return get(requisition.getId());
    }

    private RequisitionDetail detail(RequisitionSummary summary) {
        return new RequisitionDetail(summary.id(), summary.requisitionNumber(), summary.requesterId(),
                summary.requesterName(), summary.statusCode(), summary.statusName(), summary.title(),
                summary.justification(), summary.neededByDate(), items.findAllDtosByRequisitionId(summary.id()));
    }
}
