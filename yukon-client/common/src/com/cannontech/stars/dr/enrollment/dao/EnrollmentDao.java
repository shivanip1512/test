package com.cannontech.stars.dr.enrollment.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.google.common.collect.Multimap;

public interface EnrollmentDao {

    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId);

    public List<ProgramEnrollment> getActiveEnrollmentsByInventory(int inventoryId);

    /**
     * Find enrollments which were active at the given time for the given
     * account and inventory.
     */
    public List<ProgramEnrollment> getHistoricalEnrollmentsByInventoryId(
            int inventoryId, Instant when);

    /**
     * Get a list of currently enrolled programs in the given appliance category
     * for the given account which can be used to unenroll them. This should be
     * called when multiple programs per appliance category is not allowed to
     * get the programs which will need to be unenrolled to complete the given
     * program enrollment.
     */
    public List<ProgramEnrollment> findConflictingEnrollments(int accountId,
            int assignedProgramId);

    /**
     * Method to get a list of inventory ids for inventory that was opted out for a given
     * program during a given time period
     * @param program - Program to get opt outs for
     * @param startDate - Start of time period to check for opt outs (inclusive)
     * @param stopDate - End of time period to check for opt outs (inclusive)
     * @return List of inventory ids
     */
    public List<Integer> getOptedOutInventory(Program program, Date startDate, Date stopDate);

    /**
     * Method to get a list of all currently opted out inventory
     * @return List of opted out inventory ids
     */
    public List<Integer> getCurrentlyOptedOutInventory();

    /**
     * Method to get a list of programs that were enrolled in during a given time period (or are currently enrolled in the case of null start AND stop).
     * startTime and stopTime are optional (see notes below for usage).
     *
     * There are 4 possible combinations for supplying the datetimes. In the case of passing null for both the logic is slightly different, see below:
     *
     * 1. startTime = null, stopTime = null: Get all currently enrolled programs. The program must have been started, and either the stop time is in the future, is set to null.
     *
     * 2. startTime != null, stopTime != null: Get all programs that were enrolled at any point within the statTime-stopTime range, inclusive.
     * 3. startTime != null, stopTime = null: Get all programs that were enrolled at any point within the statTime-NOW range, inclusive.
     * 4. startTime = null, stopTime != null: Get all programs that were enrolled at any point within the EPOCH-stopTime range, inclusive.
     *
     * @param inventoryId - Inventory to get programs for
     * @param startTime - Start of time period (inclusive)
     * @param stopTime - End of time period (inclusive)
     */
	public List<Program> getEnrolledProgramIdsByInventory(Integer inventoryId,
			Date startTime, Date stopTime);

	/**
	 * Method to get a map of all programIds and their count of all inventory that is actively enrolled
	 *  but is not opted out for the given time period
	 * @param startDate
	 * @param stopDate
	 * @return
	 */
	public Map<Integer, Integer> getActiveEnrollmentExcludeOptOutCount(Date startDate, Date stopDate);

    /**
     * Determine if the given inventory item is in service.
     * @param inventoryId The id of the inventory item to check.
     * @return true if the inventory is in service.
     */
	public boolean isInService(int inventoryId);

	/**
	 * Determine if the given inventory item is in enrolled in anything.
	 * @param inventoryId The id of the inventory item to check.
	 * @return true if the inventory is in enrolled.
	 */
	public boolean isEnrolled(int inventoryId);

	/**
	 * Returns true if the account is enrolled in any program,
	 * false if not enrolled.
	 * @param accountId Id of account to check enrollment.
	 * @return True if account is enrolled, false otherwise.
	 */
    public boolean isAccountEnrolled(int accountId);

    /**
     * Returns a Set of the inventory Ids which are actively enrolled for a given group.
     *
     * @return
     */
    public Set<Integer> getActiveEnrolledInventoryIdsForGroupIds(Collection<Integer> groupIds);

    /**
     * Get a list of current enrollments in the different appliance category but for same devices selected
     * This method is used to get the programs which will need to be unenrolled to complete the given program
     * enrollment.
     * @param accountId Id of account to check enroll
     * @param assignedProgramId Id of the program to be enrolled with
     * @param inventoryIds list of the device Ids to be enrolled
     * @param assignedProgramCategoryId Id of the the appliance category of the Program
     * @returns list of conflicting programs having same devices enrolled
     */
    List<ProgramEnrollment> findOtherApplianceConflictingEnrollments(int accountId, int assignedProgramId,
            List<Integer> inventoryIds , int assignedProgramCategoryId);

    /**
     * Returns the map of group ids to inventory ids
     */

    Multimap<Integer, Integer> getActiveEnrolledInventoryIdsMapForGroupIds(Collection<Integer> groupIds);
}