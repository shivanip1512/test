package com.cannontech.cbc.cyme.model;

import com.cannontech.core.dao.NotFoundException;

public enum CapControlOperation {
    OPENBANK(1),
    CLOSEBANK(2),
    RAISETAP(3),
    LOWERTAP(4),
    SCAN(5),
    REFRESHSYSTEM(6);
    
    private int operationId;
    
    private CapControlOperation(int operationId) {
        this.operationId = operationId;
    }
    
    public int getOperationId() {
        return operationId;
    }
    
    public static CapControlOperation getByOperationId(int operationId) {
        for (CapControlOperation operation : values()) {
            if (operationId == operation.getOperationId()) {
                return operation;
            }
        }
        throw new NotFoundException("Operation not found with Id: " + operationId);
    }
}
