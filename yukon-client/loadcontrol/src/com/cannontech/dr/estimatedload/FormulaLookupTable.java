package com.cannontech.dr.estimatedload;

import com.google.common.collect.ImmutableMap;

public final class FormulaLookupTable<T> {

    private final Integer lookupTableId;
    private final int formulaId;
    private final String name;
    private final FormulaInput<T> input;
    private final ImmutableMap<T, Double> entries;

    public FormulaLookupTable(Integer lookupTableId, int formulaId, String name,
            FormulaInput<T> input, ImmutableMap<T, Double> entries) {
        this.lookupTableId = lookupTableId;
        this.formulaId = formulaId;
        this.name = name;
        this.input = input;
        this.entries = entries;
    }
    
    public Integer getLookupTableId() {
        return lookupTableId;
    }

    public int getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public FormulaInput<T> getInput() {
        return input;
    }

    public ImmutableMap<T, Double> getEntries() {
        return entries;
    }
}
