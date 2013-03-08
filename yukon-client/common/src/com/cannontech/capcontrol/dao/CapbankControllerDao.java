package com.cannontech.capcontrol.dao;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.NotFoundException;

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
     * @param deviceId the deviceId of the CBC who is allowed to have the serialNumber (if any.)
     * @param serialNumber the serial number in question.
     * @return true if the serial number doesn't already exist in the database, false otherwise.
     */
    public boolean isSerialNumberValid(Integer deviceId, int serialNumber);
    
    /**
     * This method checks to see if a serial number is valid for a new CBC.
     * @param serialNumber the serial number in question.
     * @return true if the serial number doesn't already exist in the database, false otherwise.
     */
    public boolean isSerialNumberValid(int serialNumber);
	
	public SearchResult<LiteCapControlObject> getOrphans(final int start,final int count);
	
	/**
	 * Looks up the subbus that the cbc is attached to.
	 * 
	 * @param cbcPaoId the paoId of the CBC
	 * @return the paoidentifier of the parent bus
	 * @throws NotFoundException if orphaned
	 */
	public PaoIdentifier getParentBus(int cbcPaoId);
}