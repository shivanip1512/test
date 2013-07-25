package com.cannontech.dr.estimatedload;

import java.math.BigDecimal;

public final class FormulaFunction {

    private int functionId;
    private int formulaId;
    private FormulaInput input;
    private BigDecimal inputMin;
    private BigDecimal inputMax;
    private BigDecimal quadratic;
    private BigDecimal linear;
    private BigDecimal constant;

    public FormulaFunction(int functionId, int formulaId, FormulaInput input, BigDecimal inputMin, BigDecimal inputMax,
            BigDecimal quadratic, BigDecimal linear, BigDecimal constant) {
        this.functionId = functionId;
        this.formulaId = formulaId;
        this.input = input;
        this.inputMin = inputMin;
        this.inputMax = inputMax;
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

    public BigDecimal getInputMin() {
        return inputMin;
    }

    public BigDecimal getInputMax() {
        return inputMax;
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
