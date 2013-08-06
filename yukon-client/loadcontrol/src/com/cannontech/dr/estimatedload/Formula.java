package com.cannontech.dr.estimatedload;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableList;

public final class Formula {

    public enum Type implements DisplayableEnum {
        APPLIANCE_CATEGORY,
        GEAR;

        @Override
        public String getFormatKey() {
            return "yukon.dr.estimatedLoad.formulaType." + name();
        }
    }
    
    public enum CalculationType implements DisplayableEnum {
        FUNCTION,
        LOOKUP;

        @Override
        public String getFormatKey() {
            return "yukon.dr.estimatedLoad.calculationType." + name();
        }
    }
    
    private final Integer formulaId;
    private final String name;
    private final Type formulaType;
    private final CalculationType calculationType;
    private final Double functionIntercept;

    private final ImmutableList<FormulaFunction> functions;
    private final ImmutableList<FormulaLookupTable<Object>> tables;

    public Formula(Integer formulaId, String name, Type formulaType, CalculationType calculationType, Double functionIntercept,
            ImmutableList<FormulaFunction> functions, ImmutableList<FormulaLookupTable<Object>> tables) {
        this.formulaId = formulaId;
        this.name = name;
        this.formulaType = formulaType;
        this.calculationType = calculationType;
        this.functionIntercept = functionIntercept;
        this.functions = functions;
        this.tables = tables;
    }

    public Integer getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public Type getFormulaType() {
        return formulaType;
    }

    public CalculationType getCalculationType() {
        return calculationType;
    }

    public Double getFunctionIntercept() {
        return functionIntercept;
    }

    public ImmutableList<FormulaFunction> getFunctions() {
        return functions;
    }

    public ImmutableList<FormulaLookupTable<Object>> getTables() {
        return tables;
    }
}
