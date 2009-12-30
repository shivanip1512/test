package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;

public interface StrategyDao {

    public List<CapControlStrategy> getAllStrategies();

    public boolean delete(int strategyId);

    public boolean deleteStrategyAssignmentsByStrategyId(int strategyId);
    
    public int add(String name);

    public void savePeakSettings(List<PeakTargetSetting> targetSettings, int strategyId);

    public List<PeakTargetSetting> getPeakSettings(CapControlStrategy capControlStrategy);
}
