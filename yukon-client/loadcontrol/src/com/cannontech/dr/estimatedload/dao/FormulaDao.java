package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.Formula;

public interface FormulaDao {

    public List<Formula> getAllFormulas();

    public int saveFormula(Formula formula);

    public Formula getFormulaById(int formulaId);

    public void deleteFormulaById(int formulaId);

}
