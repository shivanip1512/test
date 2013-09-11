package com.cannontech.dr.estimatedload.service.impl;

import java.util.Map;

import org.jfree.util.Log;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInputHolder;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class FormulaServiceImpl implements FormulaService {

    public FormulaInputHolder buildFormulaInputHolder(Formula formula) {
        DateTimeFormatter dtFormatter = ISODateTimeFormat.hourMinute();
        Map<Integer, LocalTime> timeTableTestValues = Maps.newHashMap();
        timeTableTestValues.put(2, LocalTime.parse("04:00", dtFormatter));
        
        FormulaInputHolder.Builder inputHolderBuilder = new FormulaInputHolder.Builder();
        
        // Build input holder for the formula
        if (formula.getFunctions() != null) {
            for (FormulaFunction function : formula.getFunctions()) {
                // TODO: Actually gather inputs here instead of averaging.
                Double average = (function.getInput().getMax() + function.getInput().getMin()) / 2;
                inputHolderBuilder.addFunctionInputValue(
                        function.getFunctionId(),
                        function.getInput(),
                        average);
                        //functionTestValues.get(function.getFunctionId()));
            }
        }
        if (formula.getTables() != null) {
            for (FormulaLookupTable<Double> table : formula.getTables()) {
                // TODO: Actually gather inputs here instead of averaging.
                Double average = (table.getInput().getMax() + table.getInput().getMin()) / 2;
                inputHolderBuilder.addTableInputValue(
                        table.getLookupTableId(),
                        table.getInput(),
                        average);
                        //tableTestValues.get(table.getLookupTableId()));
            }
        }
        if (formula.getTimeTables() != null) {
            for (FormulaLookupTable<LocalTime> timeTable : formula.getTimeTables()) {
                // TODO: Actually gather inputs here instead of averaging.
                inputHolderBuilder.addTimeTableInputValue(
                        timeTable.getLookupTableId(), 
                        timeTable.getInput(),  
                        timeTableTestValues.get(timeTable.getLookupTableId()));
            }
        }
        
        return inputHolderBuilder.build();
    }


    public YukonMessageSourceResolvable checkValidity(Formula formula, FormulaInputHolder inputHolder) {
        if (checkAllInputsValid(inputHolder)) {
            if(Log.isDebugEnabled()) {
                Log.debug("Formula: " + formula.getName() + "/" + formula.getFormulaId()
                        + " - Formula inputs ARE valid.");
            }
            return null;
        } else {
            if (Log.isDebugEnabled()) {
                Log.debug("Formula: " + formula.getName() + "/" + formula.getFormulaId()
                        + " - Formula inputs are NOT valid.");
            }
            YukonMessageSourceResolvable errorMsg = new YukonMessageSourceResolvable(
                    "yukon.web.modules.dr.estimatedLoad.calcErrors.invalidInput", formula.getName());
            return errorMsg; 
        }
    }

    /**
     * Ensures that all inputs needed to calculate the output of a formula fall within the allowable ranges defined
     * by each function or lookup table. 
     * 
     * @param formulaInputHolder The input holder object which contains minimum and maximum allowable values and the
     * current input value.
     * @return Returns true if all inputs are within a valid range.
     */
    private boolean checkAllInputsValid(FormulaInputHolder formulaInputHolder) {
        if (formulaInputHolder.getFunctionInputs() != null
                && formulaInputHolder.getFunctionInputs().keySet().size() > 0) {
            for (Integer functionId : formulaInputHolder.getFunctionInputs().keySet()) {
                Double min = formulaInputHolder.getFunctionInputs().get(functionId).getMin();
                Double max = formulaInputHolder.getFunctionInputs().get(functionId).getMax();
                Double value = formulaInputHolder.getFunctionInputValues().get(functionId);
                
                if (value < min || value > max) {
                    return false;
                }
            }
        }
        if (formulaInputHolder.getTableInputs() != null && formulaInputHolder.getTableInputs().keySet().size() > 0) {
            for (Integer tableId : formulaInputHolder.getTableInputs().keySet()) {
                Double min = formulaInputHolder.getTableInputs().get(tableId).getMin();
                Double max = formulaInputHolder.getTableInputs().get(tableId).getMax();
                Double value = formulaInputHolder.getTableInputValues().get(tableId);

                if (value < min || value > max) {
                    return false;
                }
            }
        }
        if (formulaInputHolder.getTimeTableInputs() != null
                && formulaInputHolder.getTimeTableInputs().keySet().size() > 0) {
            for (Integer timeTableId : formulaInputHolder.getTimeTableInputs().keySet()) {
                LocalTime min = formulaInputHolder.getTimeTableInputs().get(timeTableId).getMin();
                LocalTime max = formulaInputHolder.getTimeTableInputs().get(timeTableId).getMax();
                LocalTime value = formulaInputHolder.getTimeTableInputValues().get(timeTableId);

                // These comparisons need to be finished, this is not their final state.
                if (value.isBefore(min) || value.isAfter(max)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Double calculateFormulaOutput(Formula formula, FormulaInputHolder inputHolder) {
        Double output = null;
        
        if (formula.getFunctions() != null && formula.getFunctions().size() > 0) {
            Double functionOutput = 0.0;
            for (FormulaFunction f : formula.getFunctions()) {
                functionOutput += evaluateFunction(f, inputHolder.getFunctionInputValues().get(f.getFunctionId()));
            }
            // The output of this stage is the sum of all function calculations 
            // plus the overall formula function intercept.
            output = new Double(functionOutput + formula.getFunctionIntercept());
        }
        
        if (formula.getTables() != null && formula.getTables().size() > 0) {
            Double tableOutput = 0.0;
            for (FormulaLookupTable<Double> t : formula.getTables()) {
                tableOutput += evaluateTable(formula, t.getEntries(),
                        inputHolder.getTableInputValues().get(t.getLookupTableId()));
            }
            if (output == null) {
                output = new Double(0.0);
            }
            output += tableOutput;
        }
        
        /* Make sure we do not return negative values or values greater than 1.0.  Since this is the sum of
         *  all functions and table lookups, it is conceivable that this ceiling could be surpassed when many different
         *  factors are summed.
         *  Each successive stage (diversified, max kW savings) should not produce negative values or values greater
         *  than the previous stage.
         */
        if (output > 1.0) {
            output = 1.0;
        } else if (output < 0.0) {
            output = 0.0;
        }
        if(Log.isDebugEnabled()) {
            Log.debug("Formula: " + formula.getName() + "\tOutput : " + output);
        }
        return output;
    }

    /** 
     * Calculates the result of a quadratic expression: a*x^2 + b*x.  The constant term is not specified at this
     * level, it is specified only once for an entire Formula.
     * 
     * @param f The quadratic FormulaFunction that is being evaluated.
     * @param inputValue The input value for the input variable for the function. 
     * @return The output of the function as a Double. 
     */
    private Double evaluateFunction(FormulaFunction f, Double inputValue) {
        Double outputValue = f.getQuadratic() * Math.pow(inputValue, 2) + f.getLinear() * inputValue; 
        return outputValue;
    }

    /** 
     * Looks up the output value of a given lookup table by going through its table entries and comparing the
     * input value to successive key values in the entry map.  When the input value exceeds one of the entry keys, the
     * previous entry's value is returned.
     * 
     * @param entries The entry map of an estimated load lookup table that uses Double valued inputs.
     * @param input The actual input value.
     * @return The output of the table lookup as a Double.
     */
    private Double evaluateTable(Formula f, ImmutableMap<Double, Double> entries, Double input) {
        Double outputKey = null;
        
        for (Double entryKey : entries.keySet()) {
            if (input >= entryKey) {
                outputKey = entryKey;
            } else {
                break;
            }
        }
        return entries.get(outputKey);
    }

}
