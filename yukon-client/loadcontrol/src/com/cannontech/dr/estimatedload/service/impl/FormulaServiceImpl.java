package com.cannontech.dr.estimatedload.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationException.Type;
import com.cannontech.dr.estimatedload.EstimatedLoadCalculationInfo;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.FormulaInputHolder;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.dr.estimatedload.service.FormulaService;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.google.common.collect.ImmutableMap;

public class FormulaServiceImpl implements FormulaService {
    private final Logger log = YukonLogManager.getLogger(FormulaServiceImpl.class);
    
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private LMGearDao gearDao;
    
    @Override
    public FormulaInputHolder buildFormulaInputHolder(Formula formula, EstimatedLoadCalculationInfo calcInfo)
            throws EstimatedLoadCalculationException {
        FormulaInputHolder.Builder inputHolderBuilder = new FormulaInputHolder.Builder();
        
        if (formula.getFunctions() != null) {
            for (FormulaFunction function : formula.getFunctions()) {
                double value = getDoubleInputValue(formula, function.getInput(), calcInfo);
                inputHolderBuilder.addFunctionInputValue(
                        function.getFunctionId(),
                        function.getInput(),
                        value);
            }
        }
        if (formula.getTables() != null) {
            for (FormulaLookupTable<Double> table : formula.getTables()) {
                double value = getDoubleInputValue(formula, table.getInput(), calcInfo);
                inputHolderBuilder.addTableInputValue(
                        table.getLookupTableId(),
                        table.getInput(),
                        value);
            }
        }
        if (formula.getTimeTables() != null) {
            for (FormulaLookupTable<LocalTime> timeTable : formula.getTimeTables()) {
                LocalTime timeValue = new LocalTime(); // Use 'now' as input for time lookup tables.
                inputHolderBuilder.addTimeTableInputValue(
                        timeTable.getLookupTableId(), 
                        timeTable.getInput(),  
                        timeValue);
            }
        }
        return inputHolderBuilder.build();
    }

    /**
     * Retrieves the value of the formula input for a specific formula function.  If it is an appliance category
     * formula, this will always be point value retrieval.  If it is a gear formula, then either the gear parameter
     * or a specific point value will be retrieved.
     * 
     * @param formula The formula containing the function being computed.
     * @param function The function being computed.
     * @return The current Double value of the function's input.
     * @throws EstimatedLoadCalculationException 
     */
    private double getDoubleInputValue(Formula formula, FormulaInput<Double> input,
            EstimatedLoadCalculationInfo calcInfo) throws EstimatedLoadCalculationException {
        if (formula.getType() == Formula.Type.APPLIANCE_CATEGORY) {
            // All appliance category inputs can be obtained by point value lookup.
            try {
                return dynamicDataSource.getPointValue(input.getPointId()).getValue();
            } catch (DynamicDataAccessException e) {
                throw new EstimatedLoadCalculationException(Type.INPUT_VALUE_NOT_FOUND, formula.getName());
            }
        } else if (formula.getType() == Formula.Type.GEAR) {
            if (input.getInputType() == InputType.POINT) {
                // Look up point inputs for gear formulas the same way.
                try {
                    return dynamicDataSource.getPointValue(input.getPointId()).getValue();
                } catch (DynamicDataAccessException e) {
                    throw new EstimatedLoadCalculationException(Type.INPUT_VALUE_NOT_FOUND, formula.getName());
                }
            } else {
                // Find the control percent or ramp rate values for these gear formula inputs. 
                LMProgramDirectGear gear = gearDao.getByGearId(calcInfo.getGearId());
                if (input.getInputType() == InputType.CONTROL_PERCENT) {
                    // If control percent is the input it can be retrieved directly from the LMProgramDirectGear.
                    return gear.getMethodRate();
                } else if (input.getInputType() == InputType.RAMP_RATE) {
                    if (gear.getControlMethod() == GearControlMethod.SimpleThermostatRamping) {
                        // Simple thermostat gear's ramp rate is stored as degrees/hr in a single database field.
                        try {
                            return gearDao.getSimpleThermostatGearRampRate(calcInfo.getGearId());
                        } catch (DataAccessException e) {
                            throw new EstimatedLoadCalculationException(Type.INPUT_VALUE_NOT_FOUND, formula.getName());
                        }
                    } else if (gear.getControlMethod() == GearControlMethod.ThermostatRamping) {
                        // Thermostat ramping gear's ramp rate is stored in two fields, valueD and valueTd.
                        try {
                            ThermostatRampRateValues rampRateValues = gearDao.getThermostatGearRampRateValues(
                                    calcInfo.getGearId());
                            return rampRateValues.getRampRate();
                        } catch (DataAccessException e) {
                            throw new EstimatedLoadCalculationException(Type.INPUT_VALUE_NOT_FOUND, formula.getName());
                        }
                    }
                }
            }
        }
        // Should not be able to reach this point, but if so throw input not found exception.
        throw new EstimatedLoadCalculationException(Type.INPUT_VALUE_NOT_FOUND, formula.getName());
    }

    @Override
    public void checkAllInputsValid(Formula formula, FormulaInputHolder formulaInputHolder)
            throws EstimatedLoadCalculationException {
        for (Integer functionId : formulaInputHolder.getFunctionInputs().keySet()) {
            Double min = formulaInputHolder.getFunctionInputs().get(functionId).getMin();
            Double max = formulaInputHolder.getFunctionInputs().get(functionId).getMax();
            Double value = formulaInputHolder.getFunctionInputValues().get(functionId);
            
            if (value < min || value > max) {
                throw new EstimatedLoadCalculationException(
                        EstimatedLoadCalculationException.Type.INPUT_OUT_OF_RANGE, formula.getName(), 
                            formula.getFunctionById(functionId).getName());
            }
        }
        for (Integer tableId : formulaInputHolder.getTableInputs().keySet()) {
            Double min = formulaInputHolder.getTableInputs().get(tableId).getMin();
            Double max = formulaInputHolder.getTableInputs().get(tableId).getMax();
            Double value = formulaInputHolder.getTableInputValues().get(tableId);

            if (value < min || value > max) {
                throw new EstimatedLoadCalculationException(
                        EstimatedLoadCalculationException.Type.INPUT_OUT_OF_RANGE, formula.getName(), 
                            formula.getTableById(tableId).getName());
            }
        }
        for (Integer timeTableId : formulaInputHolder.getTimeTableInputs().keySet()) {
            LocalTime min = formulaInputHolder.getTimeTableInputs().get(timeTableId).getMin();
            LocalTime max = formulaInputHolder.getTimeTableInputs().get(timeTableId).getMax();
            LocalTime value = formulaInputHolder.getTimeTableInputValues().get(timeTableId);
            
            if (value.isBefore(min) || value.isAfter(max)) {
                throw new EstimatedLoadCalculationException(
                        EstimatedLoadCalculationException.Type.INPUT_OUT_OF_RANGE, formula.getName(), 
                            formula.getTimeTableById(timeTableId).getName());
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Formula: " + formula.getName() + "/" + formula.getFormulaId()
                    + " - Formula inputs ARE valid.");
        }
    }

    @Override
    public double calculateFormulaOutput(Formula formula, FormulaInputHolder inputHolder) {
        double output = 0.0;
        
        if (formula.getFunctions() != null && formula.getFunctions().size() > 0) {
            for (FormulaFunction f : formula.getFunctions()) {
                output += evaluateFunction(f, inputHolder.getFunctionInputValues().get(f.getFunctionId()));
            }
        }
        
        if (formula.getTables() != null && formula.getTables().size() > 0) {
            for (FormulaLookupTable<Double> t : formula.getTables()) {
                output += evaluateTable(formula, t.getEntries(),
                        inputHolder.getTableInputValues().get(t.getLookupTableId()));
            }
        }
        if (formula.getTimeTables() != null && formula.getTimeTables().size() > 0) {
            for (FormulaLookupTable<LocalTime> t : formula.getTimeTables()) {
                output += evaluateTimeTable(formula, t.getEntries(),
                        inputHolder.getTimeTableInputValues().get(t.getLookupTableId()));
            }
        }
        output += formula.getFunctionIntercept();
        
        /* Make sure we do not return values less than 0.0 or values greater than 1.0.  Since output is the sum of
         *  all functions and table lookups, it is conceivable that this ceiling could be surpassed when many different
         *  factors are summed.
         *  Diversified load should never be more than connected load and max kW savings should never be more
         *  than diversified load.  It is possible that 'kW savings now' be negative, but that value is not 
         *  calculated using the output of a formula. 
         */
        if (output > 1.0) {
            output = 1.0;
        } else if (output < 0.0) {
            output = 0.0;
        }
        if(log.isDebugEnabled()) {
            log.debug("Formula: " + formula.getName() + "\tOutput : " + output);
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
        List<Double> entryKeys = new ArrayList<>(entries.keySet().asList());
        Collections.sort(entryKeys);
        
        for (Double entryKey : entryKeys) {
            if (input >= entryKey) {
                outputKey = entryKey;
            } else {
                break;
            }
        }
        return entries.get(outputKey);
    }

    /** 
     * Looks up the output value of a given lookup table by going through its table entries and comparing the
     * input value to successive key values in the entry map.  When the input value exceeds one of the entry keys, the
     * previous entry's value is returned.
     * 
     * @param entries The entry map of an estimated load lookup table that uses LocalTime valued inputs.
     * @param input The actual input value.
     * @return The output of the table lookup as a Double.
     */
    private Double evaluateTimeTable(Formula f, ImmutableMap<LocalTime, Double> entries, LocalTime input) {
        LocalTime outputKey = null;
        List<LocalTime> entryKeys = new ArrayList<>(entries.keySet());
        Collections.sort(entryKeys);
        
        for (LocalTime entryKey : entryKeys) {
            if (input.isAfter(entryKey)) {
                outputKey = entryKey;
            } else {
                break;
            }
        }
        return entries.get(outputKey);
    }
}
