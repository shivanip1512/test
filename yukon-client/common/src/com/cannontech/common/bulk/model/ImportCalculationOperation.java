package com.cannontech.common.bulk.model;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * List of calculation operations for point import.
 */
public enum ImportCalculationOperation implements DatabaseRepresentationSource {
    ADD(CalcComponentTypes.ADDITION_OPERATION),
    SUBTRACT(CalcComponentTypes.SUBTRACTION_OPERATION),
    MULTIPLY(CalcComponentTypes.MULTIPLICATION_OPERATION),
    DIVIDE(CalcComponentTypes.DIVISION_OPERATION),
    PUSH(CalcComponentTypes.PUSH_OPERATION);
    
    private String dbValue;
    
    private ImportCalculationOperation(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDatabaseRepresentation() {
        return dbValue;
    }
}
