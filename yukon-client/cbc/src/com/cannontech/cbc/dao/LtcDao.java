package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.LoadTapChanger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.TransactionException;

public interface LtcDao {

    /**
     * Adds a LTC to the database.
     * Returns the new ltc's PAObject id if the add was successful
     * otherwise -1 if add was unsuccessful.
     * @param ltc
     * @return boolean
     * @throws TransactionException 
     */
    public int add(LoadTapChanger ltc);
    
    /**
     * Deletes a LTC from the database.
     * Returns true if the delete was successful.
     * @param ltc
     * @return boolean
     */
    public boolean delete(int id);
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);

    /**
     * Returns the name of the Load Tap Changer assigned 
     * to the give sub bus id or '(none)' if no Load Tap Changer is assigned.
     * @param subBusId
     * @return
     */
    public String getLtcName(int subBusId);

    /**
     * Unnassigns an LTC for the given LTC id.
     * @param id
     */
    public void unassignLtc(int id);
    
    /**
     * Unnassigns an LTC for the given Sub Bus id.
     * @param id
     */
    public void unassignBus(int id);

    /**
     * Assigns the given ltc to the given sub bus.
     * @param substationBusID
     * @param ltcId
     */
    public void assign(int substationBusID, int ltcId);
    
    /**
     * Returns the ltc id for a given sub bus id or 0 if no
     * ltc is assigned to the given sub bus id.
     * @param subBusId
     * @return
     */
    public int getLtcIdForSub(int subBusId);

    public List<Integer> getUnassignedLtcIds();

    public PaoIdentifier getLtcPaoIdentifierForSubBus(int busId); 
}
