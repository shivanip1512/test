package com.cannontech.cbc.cyme;

import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface CymeSimulationListener {

    public void notifyNewLoadFactor(PointValueQualityHolder pointData);
    public void notifyNewSimulation(PointValueQualityHolder pointData);
    public void notifyCbcControl(PointValueQualityHolder pointData);
}
