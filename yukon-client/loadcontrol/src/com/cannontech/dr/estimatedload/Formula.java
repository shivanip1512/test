package com.cannontech.dr.estimatedload;

import com.cannontech.dr.estimatedload.enumeration.FormulaCalculationType;
import com.cannontech.dr.estimatedload.enumeration.FormulaType;
import com.google.common.collect.ImmutableList;

public final class Formula {

    private final Integer formulaId;
    private final String name;
    private final FormulaType formulaType;
    private final FormulaCalculationType calculationType;
    private final Double functionIntercept;

    private final ImmutableList<FormulaFunction> functions;
    private final ImmutableList<FormulaLookupTable> tables;

    public Formula(Integer formulaId, String name, FormulaType formulaType, FormulaCalculationType calculationType, Double functionIntercept,
            ImmutableList<FormulaFunction> functions, ImmutableList<FormulaLookupTable> tables) {
        this.formulaId = formulaId;
        this.name = name;
        this.formulaType = formulaType;
        this.calculationType = calculationType;
        this.functionIntercept = functionIntercept;
        this.functions = functions;
        this.tables = tables;
    }
    
    private Formula(Builder builder) {
        formulaId = builder.formulaId;
        name = builder.name;
        formulaType = builder.formulaType;
        calculationType = builder.calculationType;
        functionIntercept = builder.functionIntercept;
        functions = builder.functions;
        tables = builder.tables;
    }

    public Integer getFormulaId() {
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
    
    public static class Builder {
        private final Integer formulaId;
        private final String name;
        private final FormulaType formulaType;
        private final FormulaCalculationType calculationType;
        private final Double functionIntercept;

        private ImmutableList<FormulaFunction> functions;
        private ImmutableList<FormulaLookupTable> tables;
        
        public Builder(Integer formulaId, String name, FormulaType formulaType, 
                FormulaCalculationType calculationType, Double functionIntercept) {
            this.formulaId = formulaId;
            this.name = name;
            this.formulaType = formulaType;
            this.calculationType = calculationType;
            this.functionIntercept = functionIntercept;
        }
        
        public Integer getFormulaId() {
            return formulaId;
        }
        
        public Builder setFunctions(ImmutableList<FormulaFunction> functions) {
            this.functions = functions;
            return this;
        }
        
        public Builder setTables(ImmutableList<FormulaLookupTable> tables) {
            this.tables = tables;
            return this;
        }
        
        public Formula build() {
            return new Formula(this);
        }
    }
}
