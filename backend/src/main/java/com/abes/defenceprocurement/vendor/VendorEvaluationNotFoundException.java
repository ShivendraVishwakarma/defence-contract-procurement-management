package com.abes.defenceprocurement.vendor;

public class VendorEvaluationNotFoundException extends RuntimeException {
    public VendorEvaluationNotFoundException(Long id) { super("Vendor evaluation not found: " + id); }
}
