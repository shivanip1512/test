package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.capcontrol.model.CapBankPointDelta;

public interface DynamicDataDao {

    public List<CapBankPointDelta> getAllPointDeltasForBankIds(List<Integer> bankIds);
    
    public List<Integer> getMonitorPointsForBank(int bankId);
}
