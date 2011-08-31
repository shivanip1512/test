package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface CapbankDao {
    public void add( Capbank bank );
    
    public boolean remove( Capbank bank );
    
    public boolean update( Capbank bank );
    
    public int getParentId(Capbank capbank);
    
    public int getCapBankIdByCBC(int paoId);
    
    public CapbankAdditional getCapbankAdditional(int paoId);
    
    public void addCapbankAdditional(CapbankAdditional capbankAdditional);
    
    public void updateCapbankAdditional(CapbankAdditional capbankAdditional);
    
    public void removeCapbankAdditional(int paoId);
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentFeederId( int capBankID )  throws EmptyResultDataAccessException;
    
    public boolean isSwitchedBank( Integer paoID );
    
    public boolean assignCapbank(Feeder feeder, Capbank capbank);
    
    public boolean assignCapbank(int feederId, int capbankId);

    public boolean unassignCapbank(Capbank capbank);
    
    public boolean unassignCapbank(int capbankId);
}
