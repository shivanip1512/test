package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevObject;

public interface DevObjectCreationInterface {
    public void createAll(DevDbSetupTask devDbSetupTask);
    public void logFinalExecutionDetails(DevObject devObj);
}
