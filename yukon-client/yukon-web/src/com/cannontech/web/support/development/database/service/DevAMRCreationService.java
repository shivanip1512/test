package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.database.objects.DevAMR;

public interface DevAMRCreationService {
    void executeSetup(DevAMR devAMR);
    boolean isRunning();
}
