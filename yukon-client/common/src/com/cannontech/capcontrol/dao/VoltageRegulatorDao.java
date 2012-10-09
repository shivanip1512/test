package com.cannontech.capcontrol.dao;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface VoltageRegulatorDao {

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
     * Gets the VoltChangePerTap value from the Regulator table.
     * @param regulatorId
     */
    public double getVoltChangePerTapForRegulator(int regulatorId);

    /**
     * Returns a list of all un-assigned Regulators as Search Results.
     * 
     * @param start
     * @param count
     * @return
     */
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    /**
     * @return true if the regulator is an orphan, otherwise false
     */
    public boolean isOrphan(int regulatorId);
}
