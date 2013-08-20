package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.Formula;

public interface FormulaDao {

    public List<Formula> getAllFormulas();

    /** Saves formula returning formulaId **/
    public int saveFormula(Formula formula);

    public Formula getFormulaById(int formulaId);

    public void deleteFormulaById(int formulaId);

    /** 
     * Saves appliance category ID's as assignments for formula
     */
    public void saveAppCategoryAssignmentsForId(int formulaId, List<Integer> appCategoryIds);

    /**
     * Returns a set of appliance category ID's which are assigned to this formulaId
     */
    public List<Integer> getAppCategoryAssignmentsById(int formulaId);

    /** 
     * Saves appliance category ID's as assignments for formula
     */
    public void saveGearAssignmentsForId(int formulaId, List<Integer> appCategoryIds);

    /**
     * Returns a set of appliance category ID's which are assigned to this formulaId
     */
    public List<Integer> getGearAssignmentsById(int formulaId);

//    public Map<Integer, Integer> getAllAppCategories();
//    public List<ApplianceCategory> getAllUnassignedAppCategories();

    /** Returns all LMProgramDirectGears with assignments mapped to null if no assignment */
//    public Map<LMProgramDirectGear, Integer> getAllGears();
//    public List<LMProgramDirectGear> getGearAssignmentsById(int formulaId);
//    public void saveGearAssignmentsForId(int formulaId, Set<Integer> gearAssignments);
//    public List<LMProgramDirectGear> getAllUnassignedGears();

}
