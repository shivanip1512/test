package com.cannontech.rest.api.point.request;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public enum MockCalcOperation {
    ADDITION_OPERATION("+", MockCalcCompType.OPERATION, MockCalcCompType.CONSTANT),
    SUBTRACTION_OPERATION("-", MockCalcCompType.OPERATION, MockCalcCompType.CONSTANT),
    MULTIPLICATION_OPERATION("*", MockCalcCompType.OPERATION, MockCalcCompType.CONSTANT),
    DIVISION_OPERATION("/", MockCalcCompType.OPERATION, MockCalcCompType.CONSTANT),
    PUSH_OPERATION("PUSH", MockCalcCompType.OPERATION, MockCalcCompType.CONSTANT),
    
    /*** START OF CALC FUNCTIONS  ***/
    
    ADDITION_FUNCTION ("Addition",MockCalcCompType.FUNCTION),
    SUBTRACTION_FUNCTION("Subtraction",MockCalcCompType.FUNCTION),
    MULTIPLICATION_FUNCTION("Multiplication",MockCalcCompType.FUNCTION),
    DIVISION_FUNCTION("Division",MockCalcCompType.FUNCTION),
    AND_FUNCTION("Logical AND",MockCalcCompType.FUNCTION),
    OR_FUNCTION ("Logical OR",MockCalcCompType.FUNCTION),
    NOT_FUNCTION("Logical NOT",MockCalcCompType.FUNCTION),
    XOR_FUNCTION("Logical XOR",MockCalcCompType.FUNCTION),
    
    GREATER_THAN_FUNCTION("Greater than",MockCalcCompType.FUNCTION),
    GREATER_THAN_EQUAL_TO_FUNCTION("Geq than",MockCalcCompType.FUNCTION),
    LESS_THAN_FUNCTION("Less than",MockCalcCompType.FUNCTION),
    LESS_THAN_EQUAL_TO_FUNCTION("Leq than",MockCalcCompType.FUNCTION),
    
    MIN_FUNCTION("Min",MockCalcCompType.FUNCTION),
    MAX_FUNCTION("Max",MockCalcCompType.FUNCTION),
    BASELINE_FUNCTION("Baseline",MockCalcCompType.FUNCTION),
    BASELINE_PERCENT_FUNCTION("Baseline Percent",MockCalcCompType.FUNCTION),
    
    DEMAND_AVG15_FUNCTION("DemandAvg15",MockCalcCompType.FUNCTION),
    DEMAND_AVG30_FUNCTION("DemandAvg30",MockCalcCompType.FUNCTION),
    DEMAND_AVG60_FUNCTION("DemandAvg60",MockCalcCompType.FUNCTION),
    PFACTOR_KW_KVAR_FUNCTION("P-Factor kW/kVAr",MockCalcCompType.FUNCTION),
    PFACTOR_KW_KQ_FUNCTION("P-Factor kW/kQ",MockCalcCompType.FUNCTION),
    PFACTOR_KW_KVA_FUNCTION("P-Factor kW/kVA",MockCalcCompType.FUNCTION),
    
    KVAR_FROM_KWKQ_FUNCTION("kVAr from kW/kQ",MockCalcCompType.FUNCTION),
    KVA_FROM_KWKVAR_FUNCTION("kVA from kW/kVAr",MockCalcCompType.FUNCTION),
    KVA_FROM_KWKQ_FUNCTION("kVA from kW/kQ",MockCalcCompType.FUNCTION),
    SQUARED_FUNCTION("Squared",MockCalcCompType.FUNCTION),
    SQUARE_ROOT_FUNCTION("Square Root",MockCalcCompType.FUNCTION),
    ARCTAN_FUNCTION("ArcTan",MockCalcCompType.FUNCTION),
    MAX_DIFFERENCE("Max Difference",MockCalcCompType.FUNCTION),
    ABS_VALUE("Absolute Value",MockCalcCompType.FUNCTION),
    KW_FROM_KVAKVAR_FUNCTION("kW from kVA/kVAr",MockCalcCompType.FUNCTION),
    MODULO_DIVIDE("Modulo Divide",MockCalcCompType.FUNCTION),
    STATE_TIMER_FUNCTION("State Timer",MockCalcCompType.FUNCTION),
    TRUE_FALSE_CONDITION("True,False,Condition",MockCalcCompType.FUNCTION),
    REGRESSION("Regression",MockCalcCompType.FUNCTION),
    BINARY_ENCODE("Binary Encode",MockCalcCompType.FUNCTION),
    MID_LEVEL_LATCH("Mid Level Latch",MockCalcCompType.FUNCTION),
    FLOAT_FORM_16BIT("Float From 16bit",MockCalcCompType.FUNCTION),
    GET_POINT_LIMIT("Get Point Limit",MockCalcCompType.FUNCTION),
    GET_INTERVAL_MINUTES("Get Interval Minutes",MockCalcCompType.FUNCTION),
    INTERVALS_TO_VALUE("Intervals To Value",MockCalcCompType.FUNCTION),
    LINEAR_SLOPE("Linear Slope",MockCalcCompType.FUNCTION);
    
    private String calcOperation;
    private ImmutableSet<MockCalcCompType> types;
    
    private MockCalcOperation(String calcOperation, MockCalcCompType... types) {
        this.calcOperation = calcOperation;
        this.types = ImmutableSet.copyOf(types);
    }

    static ImmutableSetMultimap<MockCalcCompType, MockCalcOperation> operationByCompType;
    private final static ImmutableMap<String, MockCalcOperation> lookupByCalcOperation;
    
    static {
        try {
            ImmutableMap.Builder<String, MockCalcOperation> calcOperationBuilder = ImmutableMap.builder();
            ImmutableSetMultimap.Builder<MockCalcCompType, MockCalcOperation> builder = ImmutableSetMultimap.builder();
            for (MockCalcOperation calcOperation : values()) {
                calcOperationBuilder.put(calcOperation.getCalcOperation(), calcOperation);
                for (MockCalcCompType calcCompType : calcOperation.getTypes()) {
                    builder.put(calcCompType, calcOperation);
                }
            }
            lookupByCalcOperation = calcOperationBuilder.build();
            operationByCompType = builder.build();
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public static ImmutableSet<MockCalcOperation> getCalcOperationsByCompType(MockCalcCompType calcCompType) {
        return operationByCompType.get(calcCompType);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MockCalcOperation getCalcOperation(String calcOperation) {
        MockCalcOperation operation = lookupByCalcOperation.get(calcOperation);
        checkArgument(operation != null, operation);
        return operation;
    }

    @JsonValue
    public String getCalcOperation() {
        return calcOperation;
    }
    
    public ImmutableSet<MockCalcCompType> getTypes() {
        return types;
    }
}
