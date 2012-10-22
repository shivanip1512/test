package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.DevDbSetupTask;

public interface DevDatabasePopulationService {
    public void executeFullDatabasePopulation(DevDbSetupTask devDbBackingBean);
    public DevDbSetupTask getExecuting();
}
