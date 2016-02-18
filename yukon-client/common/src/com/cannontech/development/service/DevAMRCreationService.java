package com.cannontech.development.service;

import com.cannontech.development.model.DevAMR;

public interface DevAMRCreationService {
    void executeSetup(DevAMR devAMR);
    boolean isRunning();
}
