package com.cannontech.dr.estimatedload;

import com.google.common.collect.ImmutableMap;

public final class FormulaLookupTable {

    private final Integer lookupTableId;
    private final int formulaId;
    private final String name;
    private final FormulaInput input;
    private final ImmutableMap<String, String> entries;

    public FormulaLookupTable(Integer lookupTableId, int formulaId, String name,
            FormulaInput input, ImmutableMap<String, String> entries) {
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

    public ImmutableMap<String, String> getEntries() {
        return entries;
    }

    public static class Builder {
        private final Integer lookupTableId;
        private final int formulaId;
        private final String name;
        private final FormulaInput input;

        private ImmutableMap<String, String> entries;

        public Builder(Integer lookupTableId, int formulaId, String name, FormulaInput input) {
            this.lookupTableId = lookupTableId;
            this.formulaId = formulaId;
            this.name = name;
            this.input = input;
        }

        public void setEntries(ImmutableMap<String, String> entries) {
            this.entries = entries;
        }

        public FormulaLookupTable build() {
            return new FormulaLookupTable(this);
        }
    }
}
