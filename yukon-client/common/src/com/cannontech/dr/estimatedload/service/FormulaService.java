package com.cannontech.dr.estimatedload.service;

import com.cannontech.dr.estimatedload.EstimatedLoadCalculationInfo;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaInputHolder;


public interface FormulaService {

    /** 
     * This method iterates through the functions and lookup tables that belong to a given Formula and attempts to
     * read all required input values from the database.  This includes environmental data such as temperature and
     * humidity, point data from any points assigned as inputs, and gear parameters such as control percentage and
     * temperature ramp rate.
     * 
     * @param formula The formula for which input values are to be gathered.
     * @param calcInfo The calculation information gathered to compute formula output.
     * @return The object which holds all of the current input values, as well as the minimum and maximum allowable
     * values for those inputs.
     * @throws EstimatedLoadCalculationException 
     */
    public FormulaInputHolder buildFormulaInputHolder(Formula formula, EstimatedLoadCalculationInfo calcInfo)
            throws EstimatedLoadException;

    /**
     * Checks that all inputs collected by the FormulaInputHolder object fall within valid input value ranges
     * as specified by the estimated load formula in question.
     *  
     * @param formula The estimated load formula object currently being evaluated.
     * @param inputHolder The object which holds all minimum and maximum input values, as well as the current
     * value of all inputs as read from RawPointHistory. 
     * @throws EstimatedLoadCalculationException 
     */
    public void checkAllInputsValid(Formula formula, FormulaInputHolder formulaInputHolder)
            throws EstimatedLoadException;

    /** 
     * Evaluates the output value of a formula by summing the output of all of the functions and lookup tables.
     * This number is capped at 0.0 and 1.0 so that the output of a formula can never be negative or greater than 100%.
     * This is because diversified load can never be larger than connected load, and max kW savings can never be larger
     * than diversified load.  Otherwise, we would be implying that a control event can deliver more load reduction
     * for a given program than there is load connected to that program, which is impossible.
     * 
     * @param formula The estimated load formula whose output is being computed.
     * @param inputHolder The input values being supplied to the functions and lookup tables for the formula.
     * @return Returns a double floating point value between 0.0 and 1.0. 
     * @throws FormulaCalculationException 
     */
    public double calculateFormulaOutput(Formula formula, FormulaInputHolder inputHolder);

}