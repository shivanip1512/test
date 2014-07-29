package com.cannontech.dr.estimatedload;

public final class InputValueNotFoundException extends EstimatedLoadException {

    private final String formulaName;

    public InputValueNotFoundException(String formulaName) {
        this.formulaName = formulaName;
    }

    public String getFormulaName() {
        return formulaName;
    }

}
