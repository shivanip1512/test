package com.cannontech.dr.estimatedload.dao;

import java.util.List;

import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
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

    /** Determines if a formula exists with a given name. */
    public boolean isFormulaNameInUse(String name);

    /** 
     * Searches formula inputs and returns true if this pao is used as an input for a formula.
     * returns false if not
     **/
    public boolean hasFormulaInputPoints(int pointId);

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
    public List<ApplianceCategoryAssignment> getAssignmentsForApplianceCategories(Iterable<Integer> appCategoryIds);

    /** Returns an ApplianceCategoryAssignment object that describes which formula is assigned
     * to the given appliance category id. */
    public ApplianceCategoryAssignment getAssignmentForApplianceCategory(int appCategoryId);

    /** Returns the Formula object assigned to a given appliance category, or NULL if none is assigned. 
     * @throws EstimatedLoadException When no appliance category formula can be found for the specified appliance
     * category pao id. 
     */
    public Formula getFormulaForApplianceCategory(int appCategoryId) throws EstimatedLoadException;
    
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

    /** Returns the Formula object assigned to a given gear, or NULL if none is assigned. 
     * @throws EstimatedLoadException When no gear formula can be found for the specified gear id.
     */
    public Formula getFormulaForGear(int gearId) throws EstimatedLoadException;

}
