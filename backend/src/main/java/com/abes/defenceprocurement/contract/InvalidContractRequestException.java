package com.abes.defenceprocurement.contract;

public class InvalidContractRequestException extends RuntimeException {
    public InvalidContractRequestException(String message) {
        super(message);
    }
}
