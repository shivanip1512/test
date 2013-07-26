package com.cannontech.dr.estimatedload;

import java.math.BigDecimal;

import com.cannontech.dr.estimatedload.enumeration.FormulaInputType;

public final class FormulaInput {

    private final FormulaInputType inputType;
    private final BigDecimal min;
    private final BigDecimal max;
    private final int pointId;
    

    public FormulaInput(FormulaInputType inputType, BigDecimal min, BigDecimal max, int pointId) {
        this.inputType = inputType;
        this.min = min;
        this.max = max;
        this.pointId = pointId;
    }

    public FormulaInputType getInputType() {
        return inputType;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public int getPointId() {
        return pointId;
    }
}
