package com.cannontech.web.dev.database.service;

import com.cannontech.web.dev.database.objects.DevAMR;

public interface DevAMRCreationService {
    void executeSetup(DevAMR devAMR);
    boolean isRunning();
}
