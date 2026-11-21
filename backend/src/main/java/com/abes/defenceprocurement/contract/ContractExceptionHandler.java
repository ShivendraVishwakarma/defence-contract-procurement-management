package com.abes.defenceprocurement.contract;

import com.abes.defenceprocurement.auth.ApiMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = ContractController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContractExceptionHandler {
    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<ApiMessage> notFound(ContractNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, InvalidContractRequestException.class})
    public ResponseEntity<ApiMessage> invalidRequest(Exception exception) {
        return ResponseEntity.badRequest().body(new ApiMessage("Contract request is invalid"));
    }
}
