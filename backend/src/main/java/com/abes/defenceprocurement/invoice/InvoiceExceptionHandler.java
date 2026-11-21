package com.abes.defenceprocurement.invoice;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackageClasses = InvoiceController.class)
public class InvoiceExceptionHandler {
    @ExceptionHandler(InvoiceNotFoundException.class)
    ResponseEntity<Map<String, String>> missing(InvoiceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
    @ExceptionHandler(InvalidInvoiceException.class)
    ResponseEntity<Map<String, String>> invalid(InvalidInvoiceException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
