package com.cannontech.dr.estimatedload;


public final class FormulaFunction {

    private final Integer functionId;
    private final Integer formulaId;
    private final String name;
    private final FormulaInput<Double> input;
    private final double quadratic;
    private final double linear;

    public FormulaFunction(Integer functionId, Integer formulaId, String name, FormulaInput<Double> input,
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

    public Integer getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public FormulaInput<Double> getInput() {
        return input;
    }

    public double getQuadratic() {
        return quadratic;
    }

    public double getLinear() {
        return linear;
    }
    
    public FormulaFunction withFormulaId(Integer formulaId) {
        return new FormulaFunction(functionId, formulaId, name, (FormulaInput<Double>) input, quadratic, linear);
    }
}
