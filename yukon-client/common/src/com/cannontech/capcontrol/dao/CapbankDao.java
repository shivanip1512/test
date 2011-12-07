package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;

public interface CapbankDao {
    
    /**
     * Returns the PaoID of the capbank whose CBC is specified by cbcId.
     * @param cbcId the paoId of the CBC whose CapBank we want the ID of.
     * @return a PaoIdentifier representing the cap bank.
     */
    public PaoIdentifier findCapBankIdByCBC(int cbcId);
    
    /**
     * Returns the capbank additional information for a CapBank
     * @param paoId the paoId of the capbank whose information is desired.
     * @return CapbankAdditional object containing the information from the database.
     */
    public CapbankAdditional getCapbankAdditional(int paoId);
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int start, int count);
    
    /**
     * This method returns the PaoIdentifier of the Feeder that owns the given cap bank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public PaoIdentifier getParentFeederIdentifier( int capBankID )  throws EmptyResultDataAccessException;
    
    /**
     * Returns whether or not the cap bank is switched.
     * @param paoID for the capbank in question.
     * @return true if the bank is switched, false if it is not.
     */
    public boolean isSwitchedBank( Integer paoID );
    
    /**
     * Assigns a capbank to a feeder and processes the necessary dbchange messages.
     * @param capbankId the id of the capbank being assigned
     * @param feederName name of the feeder being assigned to.
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignCapbank(int capbankId, String feederName);
    
    /**
     * Assigns a capbank to a feeder and processes the necessary dbchange messages.
     * @param feederId the id of the feeder being assigned to
     * @param capbankId the id of the capbank being assigned
     * @return true if the assignment occurred with only one row in the db updated, false otherwise.
     */
    public boolean assignCapbank(int feederId, int capbankId);
    
    /**
     * Removes all assignments in the database for a given capbank.
     * @param capbankId the id of the capbank being unassigned.
     * @return true if the unassignment occurred with only one row in the db 
     * updated, false otherwise.
     */
    public boolean unassignCapbank(int capbankId);
}
