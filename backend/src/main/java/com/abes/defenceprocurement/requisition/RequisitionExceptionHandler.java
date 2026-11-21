package com.abes.defenceprocurement.requisition;

import com.abes.defenceprocurement.auth.ApiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = RequisitionController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequisitionExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequisitionExceptionHandler.class);

    @ExceptionHandler({RequisitionNotFoundException.class, RequisitionItemNotFoundException.class})
    public ResponseEntity<ApiMessage> notFound(RuntimeException exception) { return ResponseEntity.notFound().build(); }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, InvalidRequisitionRequestException.class})
    public ResponseEntity<ApiMessage> invalidRequest(Exception exception) {
        logger.warn("Invalid requisition request: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(new ApiMessage("Requisition request is invalid"));
    }
}
