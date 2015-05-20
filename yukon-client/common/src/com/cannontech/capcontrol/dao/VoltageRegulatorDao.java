package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;

public interface VoltageRegulatorDao {

    public List<LiteCapControlObject> getOrphans();
    
    /**
     * @return true if the regulator is an orphan, otherwise false
     */
    public boolean isOrphan(int regulatorId);
}
