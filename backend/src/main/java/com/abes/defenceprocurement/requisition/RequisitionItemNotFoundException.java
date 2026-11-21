package com.abes.defenceprocurement.requisition;

public class RequisitionItemNotFoundException extends RuntimeException {
    public RequisitionItemNotFoundException(Long id) { super("Requisition item not found: " + id); }
}
