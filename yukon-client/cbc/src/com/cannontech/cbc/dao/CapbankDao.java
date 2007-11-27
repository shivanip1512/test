package com.cannontech.cbc.dao;

import java.util.List;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.core.dao.NotFoundException;

public interface CapbankDao {
    public boolean add( Capbank bank );
    
    public boolean remove( Capbank bank );
    
    public boolean update( Capbank bank );
    
    public Capbank getById( int id ) throws NotFoundException;
    
    /**
     * This method returns all the CapBank IDs that are not assgined
     *  to a Feeder.
     */
    public List<Integer> getUnassignedCapBankIds();
    
    /**
     * This method returns the Feeder ID that owns the given capbank ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentFeederId( int capBankID ) throws NotFoundException;
    
    public boolean isSwitchedBank( Integer paoID );
}
