package com.abes.defenceprocurement.vendor;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = VendorEvaluationController.class)
public class VendorEvaluationExceptionHandler {
    @ExceptionHandler(VendorEvaluationNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(VendorEvaluationNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(InvalidVendorEvaluationException.class)
    ResponseEntity<Map<String, String>> invalid(InvalidVendorEvaluationException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}
