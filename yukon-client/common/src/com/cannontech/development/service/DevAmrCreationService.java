package com.cannontech.development.service;

import com.cannontech.development.model.DevAmr;

public interface DevAmrCreationService {
    void executeSetup(DevAmr devAmr);
    boolean isRunning();
}
