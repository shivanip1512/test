package com.cannontech.web.dev.database.service;

import com.cannontech.web.dev.database.objects.DevCapControl;

public interface DevCapControlCreationService {
    boolean isRunning();
    int getPercentComplete();
    void executeSetup(DevCapControl devCapControl);
}
