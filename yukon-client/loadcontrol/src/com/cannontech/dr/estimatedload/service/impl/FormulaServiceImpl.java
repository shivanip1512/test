package com.cannontech.dr.estimatedload.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.service.FormulaService;

public class FormulaServiceImpl implements FormulaService {
    
    @Autowired FormulaDao formulaDao;

    @Override
    public List<Formula> getAllFormluas() {
        return formulaDao.getAllFormulas();
    }

    @Override
    public Formula getFormula(int formulaId) {
        return formulaDao.getFormulaById(formulaId);
    }

    @Override
    public int saveFormula(Formula formula) {
        return formulaDao.saveFormula(formula);
    }

    @Override
    public void deleteFormula(int formulaId) {
        formulaDao.deleteFormulaById(formulaId);
    }
}
