package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.GearAssignment;

public interface FormulaDao {

    /** Retrieves a list of all formula objects **/
    public List<Formula> getAllFormulas();

    /** Saves formula returning formulaId **/
    public int saveFormula(Formula formula);

    /** Retrieves a formula by its id **/
    public Formula getFormulaById(int formulaId);

    /** Deletes a formula by its id **/
    public void deleteFormulaById(int formulaId);

    /** 
     * Searches formula inputs and returns true if this pao is used as an input for a formula.
     * returns false if not
     **/
    public boolean isPointAFormulaInput(int pointId);

    // Handling appliance category assignments

    /** Saves appliance category ID's as assignments for formula */
    public void saveAppCategoryAssignmentsForId(int formulaId, List<Integer> appCategoryIds);

    /** Will unassign this appCategory from any formula (if currently assigned) then
     * add this assignment to the list of assignments for this formula */
    public void saveAppCategoryAssignmentForId(int formulaId, int appCategoryAssignment);

    /** Removes formula assignment for an appliance category id */
    public void unassignAppCategory(int appCategoryId);

    /** Returns a set of appliance category ID's which are assigned to this formulaId */
    public List<Integer> getAppCategoryAssignmentIds(int formulaId);

    /** Returns a list of ApplianceCategoryAssignment objects that describe which formula 
     * is assigned to each appliance category id passed in the input parameter list */
    public List<ApplianceCategoryAssignment> getAssignmentsForApplianceCategories(List<Integer> appCategoryIds);

    /** Returns an ApplianceCategoryAssignment object that describes which formula is assigned
     * to the given appliance category id. */
    public ApplianceCategoryAssignment getAssignmentForApplianceCategory(int appCategoryId);


    // Handling gear assignments

    /** Saves gear ids as assignments for formula */
    public void saveGearAssignmentsForId(int formulaId, List<Integer> appCategoryIds);

    /** Will unassign this gear from any formula (if currently assigned) then
     * add this assignment to the list of assignments for this formula */
    public void saveGearAssignmentForId(int formulaId, int gearId);

    /** Removes formula assignment for a gear id */
    public void unassignGear(int gearId);
    
    /** Returns a set of gear ids which are assigned to this formulaId */
    public List<Integer> getGearAssignmentIds(int formulaId);

    /** Returns a list of GearAssignment objects that describe which formula 
     * is assigned to each gear id passed in the input parameter list */
    public List<GearAssignment> getAssignmentsForGears(Iterable<Integer> gearIds);

    /** Returns a GearAssignment object that describes which formula is assigned
    * to the given gear id. */
    public GearAssignment getAssignmentForGear(int gearId);
}
