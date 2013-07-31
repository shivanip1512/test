package com.cannontech.dr.estimatedload.service;

import java.util.List;

import com.cannontech.dr.estimatedload.Formula;

public interface FormulaService {

    public List<Formula> getAllFormluas();

    /** Returns complete formula object */
    public Formula getFormula(int formulaId);

    /** Saves complete formula object including tables and/or functions */
    public int saveFormula(Formula formula);

    /** Delete a formula object including tables and/or functions */
    public void deleteFormula(int formulaId);

}
