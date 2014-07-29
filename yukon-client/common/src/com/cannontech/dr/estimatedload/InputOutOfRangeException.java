package com.cannontech.dr.estimatedload;

public final class InputOutOfRangeException extends EstimatedLoadException {

    private final Formula formula;
    private final Type type;
    private final int id;

    public static enum Type {
        FUNCTION,
        LOOKUP,
        TIME_LOOKUP
    }

    public InputOutOfRangeException(Formula formula, Type type, int id) {
        super();
        this.formula = formula;
        this.type = type;
        this.id = id;
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

}
