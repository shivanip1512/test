package com.cannontech.dr.estimatedload;


public final class FormulaFunction {

    private final Integer functionId;
    private final int formulaId;
    private final String name;
    private final FormulaInput<Object> input;
    private final double quadratic;
    private final double linear;

    public FormulaFunction(Integer functionId, int formulaId, String name, FormulaInput<Object> input,
            double quadratic, double linear) {
        this.functionId = functionId;
        this.formulaId = formulaId;
        this.name = name;
        this.input = input;
        this.quadratic = quadratic;
        this.linear = linear;
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

    public FormulaInput<Object> getInput() {
        return input;
    }

    public double getQuadratic() {
        return quadratic;
    }

    public double getLinear() {
        return linear;
    }

}
