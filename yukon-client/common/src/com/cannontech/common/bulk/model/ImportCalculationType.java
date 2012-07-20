package com.cannontech.common.bulk.model;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * List of calculation types for point import.
 */
public enum ImportCalculationType implements DatabaseRepresentationSource {
    OPERATION(CalcComponentTypes.OPERATION_COMP_TYPE),
    CONSTANT(CalcComponentTypes.CONSTANT_COMP_TYPE),
    FUNCTION(CalcComponentTypes.FUNCTION_COMP_TYPE);
    
    private String typeString;
    
    private ImportCalculationType(String typeString) {
        this.typeString = typeString;
    }
    
    public String getDatabaseRepresentation() {
        return typeString;
    }
}
