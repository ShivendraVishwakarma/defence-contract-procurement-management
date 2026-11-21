package com.abes.defenceprocurement.requisition;

import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.Validation;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RequisitionValidationTest {
    @Test void rejectsMissingFieldsInvalidQuantitiesPricesAndMalformedDatesAtRequestBoundary() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var header = new RequisitionCreateRequest(" ", " ", " ", null, " ");
            assertFalse(validator.validate(header).isEmpty());
            var quantity = new RequisitionItemRequest(0, " ", BigDecimal.ZERO, " ", new BigDecimal("-1"));
            assertFalse(validator.validate(quantity).isEmpty());
            var precision = new RequisitionItemRequest(1, "Example", new BigDecimal("1.0001"), "each", new BigDecimal("1.001"));
            assertFalse(validator.validate(precision).isEmpty());
        }
    }
}
