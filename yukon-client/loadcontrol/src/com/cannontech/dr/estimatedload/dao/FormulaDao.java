package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.GearAssignment;

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
     * Will unassign this appCategory from any formula (if currently assigned) then
     * add this assignment to the list of assignments for this formula
     */
    public void saveAppCategoryAssignmentForId(int formulaId, int appCategoryAssignment);
    public void unassignAppCategory(int appCategoryId);

    /**
     * Returns a set of appliance category ID's which are assigned to this formulaId
     */
    public List<Integer> getAppCategoryAssignmentIds(int formulaId);
    public List<ApplianceCategoryAssignment> getAssignmentsForApplianceCategories(List<Integer> appCategoryIds);
    public ApplianceCategoryAssignment getAssignmentForApplianceCategory(int appCategoryId);

    /**
     * Saves appliance category ID's as assignments for formula
     */
    public void saveGearAssignmentsForId(int formulaId, List<Integer> appCategoryIds);
    /**
     * Will unassign this gear from any formula (if currently assigned) then
     * add this assignment to the list of assignments for this formula
     */
    public void saveGearAssignmentForId(int formulaId, int gearId);
    public void unassignGear(int gearId);
    
    /**
     * Returns a set of appliance category ID's which are assigned to this formulaId
     */
    public List<Integer> getGearAssignmentIds(int formulaId);
    public List<GearAssignment> getAssignmentsForGears(Iterable<Integer> gearIds);
    public GearAssignment getAssignmentForGear(int gearId);
}
