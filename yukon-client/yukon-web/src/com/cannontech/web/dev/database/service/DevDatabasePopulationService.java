package com.cannontech.web.dev.database.service;

import com.cannontech.web.dev.DevDbSetupTask;

public interface DevDatabasePopulationService {
    public void executeFullDatabasePopulation(DevDbSetupTask devDbBackingBean);
    public DevDbSetupTask getExecuting();
}
