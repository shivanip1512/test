package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.DevDbSetupTask;

public interface DevObjectCreationInterface {
    public void createAll(DevDbSetupTask devDbSetupTask, boolean create);
    public abstract void createAll();
}
