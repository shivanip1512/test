package com.cannontech.stars.dr.enrollment.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;

public interface EnrollmentDao {

    public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId);

    /**
     * Method to get a list of all the programs a device is currently enrolled in
     * @param inventoryId - Inventory in question
     * @return List of enrolled programs
     */
    public List<Program> getCurrentlyEnrolledProgramsByInventoryId(int inventoryId);
    
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
     * Method to get opt out history by program and time period
     * @param program - Program to get history for
     * @param startDate - Start of time period (inclusive)
     * @param stopDate - End of time period (inclusive)
     * @return List of history
     */
    public List<LMHardwareControlGroup> getOptOutHistoryByProgram(
    		Program program, Date startDate, Date stopDate);

    /**
     * Method to get a list of programs an inventory was enrolled in during a given time period
     * NOTE: startTime and stopTime are required. 
     * Use {@link EnrollmentDao#getAllEnrolledProgramIdsByInventory(Integer, Date, Date) getAllEnrolledProgramIdsByInventory} for optinal date range.
     * @param inventoryId - Inventory to get programs for
     * @param startTime - Start of time period (inclusive)
     * @param stopTime - End of time period (inclusive)
     */
	public List<Program> getEnrolledProgramIdsByInventory(Integer inventoryId,
			Date startTime, Date stopTime);
	
	/**
	 * Method to get a list of programs an inventory was enrolled in during a given time period
	 * If only the start date is provided, then only those devices enrolled after the provided date up through the current date will be returned. 
	 * If only the stop date is provided, then only those devices enrolled from the system origin date up to the provided stop date will be enrolled. 
	 * If no dates are provided, then all currently enrolled devices will be returned. 
	 * @param inventoryId
	 * @param startTime
	 * @param stopTime
	 * @return
	 */
	public List<Program> getAllEnrolledProgramIdsByInventory(Integer inventoryId, Date startTime, Date stopTime);

	/**
	 * Method to get a map of all programIds and their count of all inventory that is actively enrolled 
	 *  but is not opted out for the given time period
	 * @param startDate
	 * @param stopDate
	 * @return
	 */
	public Map<Integer, Integer> getActiveEnrollmentExcludeOptOutCount(Date startDate, Date stopDate);
}