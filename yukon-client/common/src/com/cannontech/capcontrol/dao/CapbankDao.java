package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface CapbankDao {
    
    /**
     * Returns the PaoID of the capbank whose CBC is specified by cbcId.
     * @param cbcId the paoId of the CBC whose CapBank we want the ID of.
     * @return a PaoIdentifier representing the cap bank.
     */
    public PaoIdentifier findCapBankByCbc(int cbcId);
    
    /**
     * Returns the capbank additional information for a CapBank
     * @param paoId the paoId of the capbank whose information is desired.
     * @return CapbankAdditional object containing the information from the database.
     */
    public CapbankAdditional getCapbankAdditional(int paoId);
    
    /**
     * This method returns all the CapBanks that are not assigned to a Feeder.
     */
    public List<LiteYukonPAObject> getUnassignedCapBanks();
    
    public List<LiteCapControlObject> getOrphans();
    
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
     * Assigns a capbank to a feeder and processes the necessary dbchange
     * messages.
     * @param capbank the YukonPao of the capbank being assigned
     * @param feederName name of the feeder being assigned to.
     * @return true if the assignment occurred with only one row in the db
     *         updated, false otherwise.
     */
    public boolean assignCapbank(YukonPao capbank, String feederName);
    
    /**
     * Assigns a capbank to a feeder and processes the necessary dbchange
     * messages.
     * @param feeder the YukonPao of the feeder being assigned to
     * @param capbank the YukonPao of the capbank being assigned
     * @return true if the assignment occurred with only one row in the db
     *         updated, false otherwise.
     */
    public boolean assignCapbank(YukonPao feeder, YukonPao capbank);
    
    /**
     * Removes all assignments in the database for a given capbank.
     * @param capbank the YukonPao of the capbank being unassigned.
     * @return true if the unassignment occurred with only one row in the db
     *         updated, false otherwise.
     */
    public boolean unassignCapbank(YukonPao capbank);
    
    /**
     * Removes all capbank assignments in the database for a given feeder id.
     * @param feederId the feederId to unassign capbanks for
     */
    public void unassignCapbanksForFeeder(int feederId);
    
    /**
     * Looks up the subbus that the bank is attached to.
     * 
     * @param bankId the paoId of the Cap Bank
     * @return
     * @throws NotFoundException if orphaned
     */
    public PaoIdentifier getParentBus(int bankId);

    /**
     * Assigns a capbank to a feeder and processes the necessary dbchange
     * messages.
     * @param feeder the YukonPao of the feeder being assigned to
     * @param capbank the YukonPao of the capbank being assigned
     * @param controlOrder the order of the control
     * @param closeOrder the close order for the control
     * @param tripOrder the trip order for the coontrol
     * @return true if the assignment occurred with only one row in the db
     *         updated, false otherwise.
     */
    boolean assignAndOrderCapbank(YukonPao feeder, YukonPao capbank, float controlOrder, float closeOrder, float tripOrder);

    /**
     * Assigns a capbank to a feeder and processes the necessary dbchange
     * messages.
     * @param feederId the id of the feeder being assigned to
     * @param capbankId the id of the capbank being assigned
     * @return true if the assignment occurred with only one row in the db
     *         updated, false otherwise.
     */
    boolean assignCapbank(int feederId, int capbankId);

}
