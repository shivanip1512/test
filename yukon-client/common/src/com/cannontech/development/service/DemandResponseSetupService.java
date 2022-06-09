package com.cannontech.development.service;

import com.cannontech.development.model.DemandResponseSetup;

public interface DemandResponseSetupService {
    void executeSetup(DemandResponseSetup drSetup);
    boolean isRunning();
}