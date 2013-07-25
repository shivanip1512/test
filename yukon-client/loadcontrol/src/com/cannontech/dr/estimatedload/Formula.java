package com.cannontech.dr.estimatedload;

import com.google.common.collect.ImmutableList;

public final class Formula {

    private int formulaId;
    private String name;
    private FormulaType formulaType;
    private FormulaCalculationType calculationType;

    private ImmutableList<FormulaFunction> functions = null;
    private ImmutableList<FormulaLookupTable> tables = null;

    public Formula(int formulaId, String name, FormulaType formulaType, FormulaCalculationType calculationType,
            ImmutableList<FormulaFunction> functions, ImmutableList<FormulaLookupTable> tables) {
        this.formulaId = formulaId;
        this.name = name;
        this.formulaType = formulaType;
        this.calculationType = calculationType;
        this.functions = functions;
        this.tables = tables;
    }

    public int getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public FormulaType getFormulaType() {
        return formulaType;
    }

    public FormulaCalculationType getCalculationType() {
        return calculationType;
    }

    public ImmutableList<FormulaFunction> getFunctions() {
        return functions;
    }

    public ImmutableList<FormulaLookupTable> getTables() {
        return tables;
    }

}
