package com.cannontech.capcontrol.dao;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface CapbankControllerDao {
	
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param controllerId the id of the CBC being assigned
     * @param capbankName the name of the capbank being assigned to.
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int controllerId, String capbankName);
    
    /**
     * Assigns a cbc to a capbank and performs the necessary dbchange message sending.
     * @param capbankId the id of the capbank being assigned to.
     * @param controllerId the id of the CBC being assigned
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignController(int capbankId, int controllerId);

    /**
     * Removes all assignments for the given controller
     * @param controller the paoId of the CBC
     * @return true if the unassignment occurred with only one row in the db 
     * updated, false otherwise.
     */
    public boolean unassignController(int controller);
    
    /**
     * This method checks to see if a serial number is valid for a CBC (that is, it checks
     * to see that no other CBC in the database has the same serial number.)
     * @param cbcName the name of the CBC we're checking (if the CBC named cbcName has the serial
     *    number in the database it's allowable, someone is essentially changing the CBC's serial
     *    number to the same value.
     * @param serialNumber the serial number in question.
     * @return true if the serial number doesn't already exist in the database, false otherwise.
     */
    public boolean isSerialNumberValid(String cbcName, int serialNumber);
	
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
}