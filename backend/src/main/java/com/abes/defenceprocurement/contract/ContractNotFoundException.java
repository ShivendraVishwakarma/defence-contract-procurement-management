package com.abes.defenceprocurement.contract;

public class ContractNotFoundException extends RuntimeException {
    public ContractNotFoundException(Long id) {
        super("Contract not found: " + id);
    }
}
