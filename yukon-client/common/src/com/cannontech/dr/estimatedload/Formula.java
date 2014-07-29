package com.cannontech.dr.estimatedload;

import org.joda.time.LocalTime;

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
    private final Type type;
    private final CalculationType calculationType;
    private final Double functionIntercept;

    private final ImmutableList<FormulaFunction> functions;
    private final ImmutableList<FormulaLookupTable<Double>> tables;
    private final ImmutableList<FormulaLookupTable<LocalTime>> timeTables;

    public Formula(Integer formulaId, String name, Type type, CalculationType calculationType, Double functionIntercept,
            ImmutableList<FormulaFunction> functions, 
            ImmutableList<FormulaLookupTable<Double>> tables, 
            ImmutableList<FormulaLookupTable<LocalTime>> timeTables) {

        this.formulaId = formulaId;
        this.name = name;
        this.type = type;
        this.calculationType = calculationType;
        this.functionIntercept = functionIntercept;
        this.functions = functions;
        this.tables = tables;
        this.timeTables = timeTables;
    }

    public Integer getFormulaId() {
        return formulaId;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
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

    public ImmutableList<FormulaLookupTable<Double>> getTables() {
        return tables;
    }

    public ImmutableList<FormulaLookupTable<LocalTime>> getTimeTables() {
        return timeTables;
    }

    /**
     * This method returns the FormulaFunction with a given EstimatedLoadFunctionId contained in a Formula.
     * Returns null if the requested function id is not contained in the formula.
     * 
     * @param functionId The FormulaFunction object id being requested.
     */
    public FormulaFunction getFunctionById(int functionId) {
        for (FormulaFunction f : functions) {
            if (f.getFunctionId() == functionId) {
                return f;
            }
        }
        return null;
    }

    /**
     * This method returns the FormulaLookupTable<Double> with a given EstimatedLoadFunctionId contained in a Formula.
     * Returns null if the requested table id is not contained in the formula.
     * 
     * @param functionId The FormulaLookupTable object id being requested.
     */
    public FormulaLookupTable<Double> getTableById(int tableId) {
        for (FormulaLookupTable<Double> t : tables) {
            if (t.getLookupTableId() == tableId) {
                return t;
            }
        }
        return null;
    }

    /**
     * This method returns the FormulaLookupTable<LocalTime> with a given EstimatedLoadFunctionId contained in a Formula.
     * Returns null if the requested time table id is not contained in the formula.
     * 
     * @param functionId The FormulaLookupTable object id being requested.
     */
    public FormulaLookupTable<LocalTime> getTimeTableById(int timeTableId) {
        for (FormulaLookupTable<LocalTime> t : timeTables) {
            if (t.getLookupTableId() == timeTableId) {
                return t;
            }
        }
        return null;
    }
}
