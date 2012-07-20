package com.cannontech.common.bulk.model;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * List of calculation functions for point import.
 */
public enum ImportCalculationFunction implements DatabaseRepresentationSource{
    ADD(CalcComponentTypes.ADDITION_FUNCTION),
    SUBTRACT(CalcComponentTypes.SUBTRACTION_FUNCTION),
    MULTIPLY(CalcComponentTypes.MULTIPLICATION_FUNCTION),
    DIVIDE(CalcComponentTypes.DIVISION_FUNCTION),
    AND(CalcComponentTypes.AND_FUNCTION),
    OR(CalcComponentTypes.OR_FUNCTION),
    NOT(CalcComponentTypes.NOT_FUNCTION),
    XOR(CalcComponentTypes.XOR_FUNCTION),
    GREATER_THAN(CalcComponentTypes.GREATER_THAN_FUNCTION),
    GREATER_THAN_OR_EQUAL(CalcComponentTypes.GREATER_THAN_EQUAL_TO_FUNCTION),
    LESS_THAN(CalcComponentTypes.LESS_THAN_FUNCTION),
    LESS_THAN_OR_EQUAL(CalcComponentTypes.LESS_THAN_EQUAL_TO_FUNCTION),
    MIN(CalcComponentTypes.MIN_FUNCTION),
    MAX(CalcComponentTypes.MAX_FUNCTION),
    BASELINE(CalcComponentTypes.BASELINE_FUNCTION),
    BASELINE_PERCENT(CalcComponentTypes.BASELINE_PERCENT_FUNCTION),
    DEMAND_AVG_15(CalcComponentTypes.DEMAND_AVG15_FUNCTION),
    DEMAND_AVG_30(CalcComponentTypes.DEMAND_AVG30_FUNCTION),
    DEMAND_AVG_60(CalcComponentTypes.DEMAND_AVG60_FUNCTION),
    PFACTOR_KW_KVAR(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION),
    PFACTOR_KW_KQ(CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION),
    PFACTOR_KW_KVA(CalcComponentTypes.PFACTOR_KW_KVA_FUNCTION),
    KVAR_FROM_KWKQ(CalcComponentTypes.KVAR_FROM_KWKQ_FUNCTION),
    KVA_FROM_KWKVAR(CalcComponentTypes.KVA_FROM_KWKVAR_FUNCTION),
    KVA_FROM_KWKQ(CalcComponentTypes.KVA_FROM_KWKQ_FUNCTION),
    SQUARED(CalcComponentTypes.SQUARED_FUNCTION),
    SQUARE_ROOT(CalcComponentTypes.SQUARE_ROOT_FUNCTION),
    ARCTAN(CalcComponentTypes.ARCTAN_FUNCTION),
    MAX_DIFFERENCE(CalcComponentTypes.MAX_DIFFERENCE),
    ABSOLUTE_VALUE(CalcComponentTypes.ABS_VALUE),
    KW_FROM_KVAKVAR(CalcComponentTypes.KW_FROM_KVAKVAR_FUNCTION),
    MODULO_DIVIDE(CalcComponentTypes.MODULO_DIVIDE),
    STATE_TIMER(CalcComponentTypes.STATE_TIMER_FUNCTION);
    
    private String dbValue;
    
    private ImportCalculationFunction(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDatabaseRepresentation() {
        return dbValue;
    }
}
