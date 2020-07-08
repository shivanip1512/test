package com.cannontech.web.tools.points.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum CalcOperation implements DatabaseRepresentationSource, DisplayableEnum {
    
    ADDITION_OPERATION("+"),
    SUBTRACTION_OPERATION("-"),
    MULTIPLICATION_OPERATION("*"),
    DIVISION_OPERATION("/"),
    PUSH_OPERATION("PUSH"),
    
    /*** START OF CALC FUNCTIONS  ***/
    
    ADDITION_FUNCTION ("Addition"),
    SUBTRACTION_FUNCTION("Subtraction"),
    MULTIPLICATION_FUNCTION("Multiplication"),
    DIVISION_FUNCTION("Division"),
    AND_FUNCTION("Logical AND"),
    OR_FUNCTION ("Logical OR"),
    NOT_FUNCTION("Logical NOT"),
    XOR_FUNCTION("Logical XOR"),
    
    GREATER_THAN_FUNCTION("Greater than"),
    GREATER_THAN_EQUAL_TO_FUNCTION("Geq than"),
    LESS_THAN_FUNCTION("Less than"),
    LESS_THAN_EQUAL_TO_FUNCTION("Leq than"),
    
    MIN_FUNCTION("Min"),
    MAX_FUNCTION("Max"),
    BASELINE_FUNCTION("Baseline"),
    BASELINE_PERCENT_FUNCTION("Baseline Percent"),
    
    DEMAND_AVG15_FUNCTION("DemandAvg15"),
    DEMAND_AVG30_FUNCTION("DemandAvg30"),
    DEMAND_AVG60_FUNCTION("DemandAvg60"),
    PFACTOR_KW_KVAR_FUNCTION("P-Factor kW/kVAr"),
    PFACTOR_KW_KQ_FUNCTION("P-Factor kW/kQ"),
    PFACTOR_KW_KVA_FUNCTION("P-Factor kW/kVA"),
    
    KVAR_FROM_KWKQ_FUNCTION("kVAr from kW/kQ"),
    KVA_FROM_KWKVAR_FUNCTION("kVA from kW/kVAr"),
    KVA_FROM_KWKQ_FUNCTION("kVA from kW/kQ"),
    SQUARED_FUNCTION("Squared"),
    SQUARE_ROOT_FUNCTION("Square Root"),
    ARCTAN_FUNCTION("ArcTan"),
    MAX_DIFFERENCE("Max Difference"),
    ABS_VALUE("Absolute Value"),
    KW_FROM_KVAKVAR_FUNCTION("kW from kVA/kVAr"),
    MODULO_DIVIDE("Modulo Divide"),
    STATE_TIMER_FUNCTION("State Timer"),
    TRUE_FALSE_CONDITION("True,False,Condition"),
    REGRESSION("Regression"),
    BINARY_ENCODE("Binary Encode"),
    MID_LEVEL_LATCH("Mid Level Latch"),
    FLOAT_FORM_16BIT("Float From 16bit"),
    GET_POINT_LIMIT("Get Point Limit"),
    GET_INTERVAL_MINUTES("Get Interval Minutes"),
    INTERVALS_TO_VALUE("Intervals To Value"),
    LINEAR_SLOPE("Linear Slope");
    


    private static String baseKey = "yukon.common.point.operation.";

    private String calcOperation;
    
    private CalcOperation(String calcOperation) {
        this.calcOperation = calcOperation;
    }
    private static final Logger log = YukonLogManager.getLogger(CalcOperation.class);
    private final static ImmutableMap<String, CalcOperation> lookupByCalcOperation;
    
    static {
        try {
            ImmutableMap.Builder<String, CalcOperation> calcOperationBuilder = ImmutableMap.builder();
            for (CalcOperation calcOperation : values()) {
                calcOperationBuilder.put(calcOperation.getCalcOperation(), calcOperation);
            }
            lookupByCalcOperation = calcOperationBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name.", e);
            throw e;
        }
    }

    public static CalcOperation getCalcOperation(String calcOperation) {
        CalcOperation updateType = lookupByCalcOperation.get(calcOperation);
        checkArgument(updateType != null, updateType);
        return updateType;
    }
    
    public String getCalcOperation() {
        return calcOperation;
    }

    public void setCalcOperation(String calcOperation) {
        this.calcOperation = calcOperation;
    }
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
       return getCalcOperation();
    }
    
    //Calc Functions
    public static CalcOperation[] CALC_FUNCTIONS =
        {
                ABS_VALUE,
                ADDITION_FUNCTION,
                SUBTRACTION_FUNCTION,
                MULTIPLICATION_FUNCTION,
                DIVISION_FUNCTION,
                MIN_FUNCTION,
                MAX_FUNCTION,
                MAX_DIFFERENCE,
                MODULO_DIVIDE,
                GREATER_THAN_FUNCTION,
                GREATER_THAN_EQUAL_TO_FUNCTION,
                LESS_THAN_FUNCTION,
                LESS_THAN_EQUAL_TO_FUNCTION,
                AND_FUNCTION,
                OR_FUNCTION,
                NOT_FUNCTION,
                XOR_FUNCTION,
                ARCTAN_FUNCTION,
                BASELINE_FUNCTION,
                BASELINE_PERCENT_FUNCTION,
                DEMAND_AVG15_FUNCTION,
                DEMAND_AVG30_FUNCTION,
                DEMAND_AVG60_FUNCTION,

                KVAR_FROM_KWKQ_FUNCTION,
                KVA_FROM_KWKVAR_FUNCTION,
                KVA_FROM_KWKQ_FUNCTION,
                KW_FROM_KVAKVAR_FUNCTION,
                PFACTOR_KW_KVAR_FUNCTION,
                PFACTOR_KW_KQ_FUNCTION,
                PFACTOR_KW_KVA_FUNCTION,
                SQUARED_FUNCTION,
                SQUARE_ROOT_FUNCTION,
                STATE_TIMER_FUNCTION,
                TRUE_FALSE_CONDITION,
                REGRESSION,
                BINARY_ENCODE,
                MID_LEVEL_LATCH,
                FLOAT_FORM_16BIT,
                GET_POINT_LIMIT,
                GET_INTERVAL_MINUTES,
                INTERVALS_TO_VALUE,
                LINEAR_SLOPE
        };


        //Calc Operations
        public static CalcOperation[] CALC_OPERATIONS =
        {
                ADDITION_OPERATION,
                SUBTRACTION_OPERATION,
                MULTIPLICATION_OPERATION,
                DIVISION_OPERATION,
                PUSH_OPERATION
        };
}
