package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;

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

    public List<LiteCapControlObject> getOrphans();
    
    /**
     * @return true if the regulator is an orphan, otherwise false
     */
    public boolean isOrphan(int regulatorId);
}
