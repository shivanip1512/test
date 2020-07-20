package com.cannontech.web.tools.points.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.core.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
public enum CalcOperation implements DatabaseRepresentationSource, DisplayableEnum {
    
    ADDITION_OPERATION("+", CalcCompType.OPERATION, CalcCompType.CONSTANT),
    SUBTRACTION_OPERATION("-", CalcCompType.OPERATION, CalcCompType.CONSTANT),
    MULTIPLICATION_OPERATION("*", CalcCompType.OPERATION, CalcCompType.CONSTANT),
    DIVISION_OPERATION("/", CalcCompType.OPERATION, CalcCompType.CONSTANT),
    PUSH_OPERATION("PUSH", CalcCompType.OPERATION, CalcCompType.CONSTANT),
    
    /*** START OF CALC FUNCTIONS  ***/
    
    ADDITION_FUNCTION ("Addition",CalcCompType.FUNCTION),
    SUBTRACTION_FUNCTION("Subtraction",CalcCompType.FUNCTION),
    MULTIPLICATION_FUNCTION("Multiplication",CalcCompType.FUNCTION),
    DIVISION_FUNCTION("Division",CalcCompType.FUNCTION),
    AND_FUNCTION("Logical AND",CalcCompType.FUNCTION),
    OR_FUNCTION ("Logical OR",CalcCompType.FUNCTION),
    NOT_FUNCTION("Logical NOT",CalcCompType.FUNCTION),
    XOR_FUNCTION("Logical XOR",CalcCompType.FUNCTION),
    
    GREATER_THAN_FUNCTION("Greater than",CalcCompType.FUNCTION),
    GREATER_THAN_EQUAL_TO_FUNCTION("Geq than",CalcCompType.FUNCTION),
    LESS_THAN_FUNCTION("Less than",CalcCompType.FUNCTION),
    LESS_THAN_EQUAL_TO_FUNCTION("Leq than",CalcCompType.FUNCTION),
    
    MIN_FUNCTION("Min",CalcCompType.FUNCTION),
    MAX_FUNCTION("Max",CalcCompType.FUNCTION),
    BASELINE_FUNCTION("Baseline",CalcCompType.FUNCTION),
    BASELINE_PERCENT_FUNCTION("Baseline Percent",CalcCompType.FUNCTION),
    
    DEMAND_AVG15_FUNCTION("DemandAvg15",CalcCompType.FUNCTION),
    DEMAND_AVG30_FUNCTION("DemandAvg30",CalcCompType.FUNCTION),
    DEMAND_AVG60_FUNCTION("DemandAvg60",CalcCompType.FUNCTION),
    PFACTOR_KW_KVAR_FUNCTION("P-Factor kW/kVAr",CalcCompType.FUNCTION),
    PFACTOR_KW_KQ_FUNCTION("P-Factor kW/kQ",CalcCompType.FUNCTION),
    PFACTOR_KW_KVA_FUNCTION("P-Factor kW/kVA",CalcCompType.FUNCTION),
    
    KVAR_FROM_KWKQ_FUNCTION("kVAr from kW/kQ",CalcCompType.FUNCTION),
    KVA_FROM_KWKVAR_FUNCTION("kVA from kW/kVAr",CalcCompType.FUNCTION),
    KVA_FROM_KWKQ_FUNCTION("kVA from kW/kQ",CalcCompType.FUNCTION),
    SQUARED_FUNCTION("Squared",CalcCompType.FUNCTION),
    SQUARE_ROOT_FUNCTION("Square Root",CalcCompType.FUNCTION),
    ARCTAN_FUNCTION("ArcTan",CalcCompType.FUNCTION),
    MAX_DIFFERENCE("Max Difference",CalcCompType.FUNCTION),
    ABS_VALUE("Absolute Value",CalcCompType.FUNCTION),
    KW_FROM_KVAKVAR_FUNCTION("kW from kVA/kVAr",CalcCompType.FUNCTION),
    MODULO_DIVIDE("Modulo Divide",CalcCompType.FUNCTION),
    STATE_TIMER_FUNCTION("State Timer",CalcCompType.FUNCTION),
    TRUE_FALSE_CONDITION("True,False,Condition",CalcCompType.FUNCTION),
    REGRESSION("Regression",CalcCompType.FUNCTION),
    BINARY_ENCODE("Binary Encode",CalcCompType.FUNCTION),
    MID_LEVEL_LATCH("Mid Level Latch",CalcCompType.FUNCTION),
    FLOAT_FORM_16BIT("Float From 16bit",CalcCompType.FUNCTION),
    GET_POINT_LIMIT("Get Point Limit",CalcCompType.FUNCTION),
    GET_INTERVAL_MINUTES("Get Interval Minutes",CalcCompType.FUNCTION),
    INTERVALS_TO_VALUE("Intervals To Value",CalcCompType.FUNCTION),
    LINEAR_SLOPE("Linear Slope",CalcCompType.FUNCTION);
    


    private static String baseKey = "yukon.common.point.operation.";

    private String calcOperation;
    private ImmutableSet<CalcCompType> types;
    
    private CalcOperation(String calcOperation, CalcCompType... types) {
        this.calcOperation = calcOperation;
        this.types = ImmutableSet.copyOf(types);
    }

    private static final Logger log = YukonLogManager.getLogger(CalcOperation.class);
    static ImmutableSetMultimap<CalcCompType, CalcOperation> operationByCompType;
    private final static ImmutableMap<String, CalcOperation> lookupByCalcOperation;
    
    static {
        try {
            ImmutableMap.Builder<String, CalcOperation> calcOperationBuilder = ImmutableMap.builder();
            ImmutableSetMultimap.Builder<CalcCompType, CalcOperation> builder = ImmutableSetMultimap.builder();
            for (CalcOperation calcOperation : values()) {
                calcOperationBuilder.put(calcOperation.getCalcOperation(), calcOperation);
                for (CalcCompType calcCompType : calcOperation.getTypes()) {
                    builder.put(calcCompType, calcOperation);
                }
            }
            lookupByCalcOperation = calcOperationBuilder.build();
            operationByCompType = builder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name.", e);
            throw e;
        }
    }

    public static ImmutableSet<CalcOperation> getCalcOperationsByCompType(CalcCompType calcCompType) {
        return operationByCompType.get(calcCompType);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CalcOperation getCalcOperation(String calcOperation) {
        CalcOperation operation = lookupByCalcOperation.get(calcOperation);
        checkArgument(operation != null, operation);
        return operation;
    }

    @JsonValue
    public String getCalcOperation() {
        return calcOperation;
    }

    public ImmutableSet<CalcCompType> getTypes() {
        return types;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
       return getCalcOperation();
    }
}
