package com.cannontech.common.rfn.simulation.service;

public interface PaoLocationSimulatorService {

    /**
     * -Deletes all location with an origin Simulator 
     * -Creates locations for all of the RF devices that do not have locations already creates
     */
    void setupLocations();
}
