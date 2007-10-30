package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.cbc.model.Capbank;

public interface CapbankDao {
    public boolean add( Capbank feeder );
    
    public boolean remove( Capbank feeder );
    
    public boolean update( Capbank feeder );
    
    public Capbank getById( int id ) throws DataAccessException;
    
    /**
     * This method returns all the CapBank IDs that are not assgined
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    /**
     * This method returns the Feeder ID that owns the given capbank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentFeederId( int capBankID );
    
    public boolean isSwitchedBank( Integer paoID );
}
