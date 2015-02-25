package com.cannontech.dr.estimatedload;

public final class InputOutOfRangeException extends EstimatedLoadException {

    private final Formula formula;
    private final Type type;
    private final int id;
    
    private final String inputValue;
    private final String minValue;
    private final String maxValue;

    public static enum Type {
        FUNCTION,
        LOOKUP,
        TIME_LOOKUP
    }

    public InputOutOfRangeException(Formula formula, Type type, int id, String inputValue, String minValue, String maxValue) {
        super();
        this.formula = formula;
        this.type = type;
        this.id = id;
        this.inputValue = inputValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Formula getFormula() {
        return formula;
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }
    
    public String getInputValue() {
        return inputValue;
    }
    
    public String getMinValue() {
        return minValue;
    }
    
    public String getMaxValue() {
        return maxValue;
    }
    
}
