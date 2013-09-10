package com.cannontech.web.dr.loadcontrol;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.google.common.collect.ImmutableList;

public class FunctionBean {
    private Integer functionId; // null if new
    private String name;
    private FormulaInput.InputType inputType;
    private double quadratic;
    private double linear;
    private double inputMin;
    private double inputMax;
    private Integer inputPointId;

    public FunctionBean() {}
    public FunctionBean(FormulaFunction function) {
        this.functionId = function.getFunctionId();
        this.name = function.getName();

        this.quadratic = function.getQuadratic();
        this.linear = function.getLinear();

        this.inputMin = function.getInput().getMin();
        this.inputMax = function.getInput().getMax();
        this.inputPointId = function.getInput().getPointId();
        this.inputType = function.getInput().getInputType();
    }

    public static List<FunctionBean> toBeanMap(ImmutableList<FormulaFunction> functions) {
        List<FunctionBean> beans = new ArrayList<>();
        if (functions == null) {
            return beans;
        }
        for (FormulaFunction function : functions) {
            beans.add(new FunctionBean(function));
        }
        return beans;
    }

    public static ImmutableList<FormulaFunction> toFormulaFunctions(List<FunctionBean> beans) {
        List<FormulaFunction> functions = new ArrayList<>();
        for (FunctionBean bean : beans) {
            functions.add(bean.getFormulaFunction());
        }
        return ImmutableList.copyOf(functions);
    }

    public FormulaFunction getFormulaFunction() {
        return new FormulaFunction(functionId, null, name,
                               new FormulaInput<>(inputType, inputMin, inputMax, inputPointId),
                               quadratic, linear);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(double quadratic) {
        this.quadratic = quadratic;
    }

    public double getLinear() {
        return linear;
    }

    public void setLinear(double linear) {
        this.linear = linear;
    }

    public double getInputMin() {
        return inputMin;
    }

    public void setInputMin(double inputMin) {
        this.inputMin = inputMin;
    }

    public double getInputMax() {
        return inputMax;
    }

    public void setInputMax(double inputMax) {
        this.inputMax = inputMax;
    }

    public Integer getInputPointId() {
        return inputPointId;
    }

    public void setInputPointId(Integer inputPointId) {
        this.inputPointId = inputPointId;
    }

    public FormulaInput.InputType getInputType() {
        return inputType;
    }

    public void setInputType(FormulaInput.InputType inputType) {
        this.inputType = inputType;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public boolean isPointType() {
        return inputType == InputType.POINT;
    }

    public boolean isHumidityType() {
        return inputType == InputType.HUMIDITY;
    }

    public boolean isTempType() {
        return inputType == InputType.TEMP_C
                || inputType == InputType.TEMP_F;
    }
}
