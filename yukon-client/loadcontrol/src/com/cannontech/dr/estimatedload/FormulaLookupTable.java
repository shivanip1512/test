package com.cannontech.dr.estimatedload;

import com.google.common.collect.ImmutableMap;

public final class FormulaLookupTable {

    private final Integer lookupTableId;
    private final int formulaId;
    private final String name;
    private final FormulaInput input;
    private final ImmutableMap<Double, Double> entries;

    public FormulaLookupTable(Integer lookupTableId, int formulaId, String name,
            FormulaInput input, ImmutableMap<Double, Double> entries) {
        this.lookupTableId = lookupTableId;
        this.formulaId = formulaId;
        this.name = name;
        this.input = input;
        this.entries = entries;
    }
    
    private FormulaLookupTable(Builder builder) {
        lookupTableId = builder.lookupTableId;
        formulaId = builder.formulaId;
        name = builder.name;
        input = builder.input;
        entries = builder.entries;
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

    public FormulaInput getInput() {
        return input;
    }

    public ImmutableMap<Double, Double> getEntries() {
        return entries;
    }

    public static class Builder {
        private final Integer lookupTableId;
        private final int formulaId;
        private final String name;
        private final FormulaInput input;

        private ImmutableMap<Double, Double> entries;

        public Builder(Integer lookupTableId, int formulaId, String name, FormulaInput input) {
            this.lookupTableId = lookupTableId;
            this.formulaId = formulaId;
            this.name = name;
            this.input = input;
        }

        public void setEntries(ImmutableMap<Double, Double> entries) {
            this.entries = entries;
        }

        public FormulaLookupTable build() {
            return new FormulaLookupTable(this);
        }
    }
}
