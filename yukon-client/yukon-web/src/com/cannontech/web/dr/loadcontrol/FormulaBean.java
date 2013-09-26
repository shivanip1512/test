package com.cannontech.web.dr.loadcontrol;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.Formula.CalculationType;
import com.cannontech.user.YukonUserContext;

public class FormulaBean {

    private Integer formulaId;
    private String name;
    private Formula.CalculationType calculationType = Formula.CalculationType.FUNCTION;
    private Formula.Type formulaType = Formula.Type.APPLIANCE_CATEGORY;
    private Double functionIntercept = 0.0;
    private List<FunctionBean> functions = LazyList.ofInstance(FunctionBean.class);
    private List<LookupTableBean> tables = LazyList.ofInstance(LookupTableBean.class);
    private List<Integer> assignments = LazyList.ofInstance(Integer.class);

    public FormulaBean() {}
    public FormulaBean(Formula formula, YukonUserContext userContext) {
        this.formulaId = formula.getFormulaId();
        this.name = formula.getName();
        this.calculationType = formula.getCalculationType();
        this.formulaType = formula.getType();
        this.functionIntercept = formula.getFunctionIntercept();
        this.functions = FunctionBean.toBeanMap(formula.getFunctions(), userContext);
        this.tables = LookupTableBean.toBeanMap(formula.getTables(), formula.getTimeTables(), userContext);
    }

    public Formula getFormula() {
        return new Formula(formulaId, name, formulaType, calculationType,
                           functionIntercept, 
                           FunctionBean.toFormulaFunctions(functions),
                           LookupTableBean.toLookupTables(tables),
                           LookupTableBean.toTimeLookupTables(tables));
    }

    public static List<FormulaBean> toBeans(List<Formula> formulas, YukonUserContext userContext) {
        List<FormulaBean> beans = new ArrayList<>();
        for (Formula formula : formulas) {
            beans.add(new FormulaBean(formula, userContext));
        }
        return beans;
    }

    public int getNumberOfEntries() {
        if (calculationType == CalculationType.FUNCTION) {
            return functions.size();
        }
        return tables.size();
    }
    
    public Integer getFormulaId() {
       return formulaId;
    }

    public void setFormulaId(Integer formulaId) {
        this.formulaId = formulaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Formula.CalculationType getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(Formula.CalculationType calculationType) {
        this.calculationType = calculationType;
    }

    public Formula.Type getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(Formula.Type formulaType) {
        this.formulaType = formulaType;
    }

    public List<Integer> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Integer> assignments) {
        this.assignments = assignments;
    }

    public List<FunctionBean> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionBean> functions) {
        this.functions = functions;
    }

    public List<LookupTableBean> getTables() {
        return tables;
    }

    public void setTables(List<LookupTableBean> tables) {
        this.tables = tables;
    }

    public Double getFunctionIntercept() {
        return functionIntercept;
    }

    public void setFunctionIntercept(Double functionIntercept) {
        this.functionIntercept = functionIntercept;
    }

    public boolean isFunctionCalculation() {
        return calculationType == CalculationType.FUNCTION;
    }

    public boolean isLookupTableCalculation() {
        return calculationType == CalculationType.LOOKUP;
    }

    public boolean isGearFormula() {
        return formulaType == Formula.Type.GEAR;
    }

    public boolean isAppCatFormula() {
        return formulaType == Formula.Type.APPLIANCE_CATEGORY;
    }
}
