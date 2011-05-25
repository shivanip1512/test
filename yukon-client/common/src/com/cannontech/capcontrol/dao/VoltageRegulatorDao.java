package com.cannontech.capcontrol.dao;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface VoltageRegulatorDao {

    /**
     * Adds a Regulator to the database.
     * 
     * @param paoId
     * @param keepAliveTimer
     * @param keepAliveConfig
     */
    public void add(int paoId, int keepAliveTimer, int keepAliveConfig);
    
    /**
     * Updates a Regulator.
     * 
     * @param paoId
     * @param keepAliveTimer
     * @param keepAliveConfig
     */
    public void update(int paoId, int keepAliveTimer, int keepAliveConfig);    

    /**
     * Gets the KeepAliveTimer value from the Regulator table.
     * @param regulatorId
     */
    public int getKeepAliveTimerForRegulator(int regulatorId);
    
    /**
     * Gets the KeepAliveConfig value from the Regulator table.
     * @param regulatorId
     */
    public int getKeepAliveConfigForRegulator(int regulatorId);

    /**
     * Deletes a Regulator from the database.
     * Returns true if the delete was successful.
     * @param VoltageRegulator
     * @return boolean
     */
    public boolean delete(int id);

    /**
     * Returns a list of all un-assigned Regulators as Search Results.
     * 
     * @param start
     * @param count
     * @return
     */
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
}
