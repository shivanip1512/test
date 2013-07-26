package com.cannontech.dr.estimatedload;

import com.google.common.collect.ImmutableMap;

public final class FormulaLookupTable {

    private final int lookupTableId;
    private final int formulaId;
    private final FormulaInput input;
    private final ImmutableMap<String, String> entries;

    public FormulaLookupTable(int lookupTableId, int formulaId, FormulaInput input, ImmutableMap<String, String> entries) {
        this.lookupTableId = lookupTableId;
        this.formulaId = formulaId;
        this.input = input;
        this.entries = entries;
    }

    public int getLookupTableId() {
        return lookupTableId;
    }

    public int getFormulaId() {
        return formulaId;
    }

    public FormulaInput getInput() {
        return input;
    }

    public ImmutableMap<String, String> getEntries() {
        return entries;
    }

}
