package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.cbc.model.Capbank;

public interface CapbankDao {
    public boolean add( Capbank bank );
    
    public boolean remove( Capbank bank );
    
    public boolean update( Capbank bank );
    
    public Capbank getById( int id );
    
    public Capbank getByControlDeviceId(int controlDeviceId) throws DataRetrievalFailureException;
    
    /**
     * This method returns all the CapBank IDs that are not assigned
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    /**
     * This method returns the Feeder ID that owns the given cap bank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentFeederId( int capBankID )  throws EmptyResultDataAccessException;
    
    public boolean isSwitchedBank( Integer paoID );
}
