package com.abes.defenceprocurement.requisition;

public class RequisitionNotFoundException extends RuntimeException {
    public RequisitionNotFoundException(Long id) { super("Requisition not found: " + id); }
}
