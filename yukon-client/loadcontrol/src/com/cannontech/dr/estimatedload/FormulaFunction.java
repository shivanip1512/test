package com.cannontech.dr.estimatedload;


public final class FormulaFunction {

    private final Integer functionId;
    private final int formulaId;
    private final String name;
    private final FormulaInput input;
    private final double quadratic;
    private final double linear;
    private final double constant;

    public FormulaFunction(Integer functionId, int formulaId, String name, FormulaInput input,
            double quadratic, double linear, double constant) {
        this.functionId = functionId;
        this.formulaId = formulaId;
        this.name = name;
        this.input = input;
        this.quadratic = quadratic;
        this.linear = linear;
        this.constant = constant;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public int getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public FormulaInput getInput() {
        return input;
    }

    public double getQuadratic() {
        return quadratic;
    }

    public double getLinear() {
        return linear;
    }

    public double getConstant() {
        return constant;
    }

}
