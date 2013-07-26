package com.cannontech.dr.estimatedload;

import java.math.BigDecimal;

public final class FormulaFunction {

    private final int functionId;
    private final int formulaId;
    private final FormulaInput input;
    private final BigDecimal quadratic;
    private final BigDecimal linear;
    private final BigDecimal constant;

    public FormulaFunction(int functionId, int formulaId, FormulaInput input,
            BigDecimal quadratic, BigDecimal linear, BigDecimal constant) {
        this.functionId = functionId;
        this.formulaId = formulaId;
        this.input = input;
        this.quadratic = quadratic;
        this.linear = linear;
        this.constant = constant;
    }

    public int getFunctionId() {
        return functionId;
    }

    public int getFormulaId() {
        return formulaId;
    }

    public FormulaInput getInput() {
        return input;
    }

    public BigDecimal getQuadratic() {
        return quadratic;
    }

    public BigDecimal getLinear() {
        return linear;
    }

    public BigDecimal getConstant() {
        return constant;
    }

}
