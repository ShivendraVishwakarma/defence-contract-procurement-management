package com.abes.defenceprocurement.invoice;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) { super("Invoice not found: " + id); }
}
