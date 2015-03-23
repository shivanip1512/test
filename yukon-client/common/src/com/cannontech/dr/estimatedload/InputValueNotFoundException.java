package com.cannontech.dr.estimatedload;

public final class InputValueNotFoundException extends EstimatedLoadException {

    private final String formulaName;
    private final int formulaId;

    public InputValueNotFoundException(String formulaName, int formulaId) {
        this.formulaName = formulaName;
        this.formulaId = formulaId;
    }

    public String getFormulaName() {
        return formulaName;
    }
    
    public int getFormulaId() {
        return formulaId;
    }

}
