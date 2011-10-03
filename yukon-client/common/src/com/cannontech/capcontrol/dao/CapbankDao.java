package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface CapbankDao {
    
    public int getParentId(Capbank capbank);
    
    public int getCapBankIdByCBC(int paoId);
    
    public CapbankAdditional getCapbankAdditional(int paoId);
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int start, int count);
    
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentFeederId( int capBankID )  throws EmptyResultDataAccessException;
    
    public boolean isSwitchedBank( Integer paoID );
    
    public boolean assignCapbank(int capbankId, String feederName);
    
    public boolean assignCapbank(int feederId, int capbankId);
    
    public boolean unassignCapbank(int capbankId);
}
