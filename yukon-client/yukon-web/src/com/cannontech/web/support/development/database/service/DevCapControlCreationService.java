package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.database.objects.DevCapControl;

public interface DevCapControlCreationService {
    boolean isRunning();
    int getPercentComplete();
    void executeSetup(DevCapControl devCapControl);
}
