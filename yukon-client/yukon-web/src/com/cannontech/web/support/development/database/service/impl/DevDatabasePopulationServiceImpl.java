package com.cannontech.web.support.development.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

public class DevDatabasePopulationServiceImpl implements DevDatabasePopulationService {
    @Autowired private DevRolePropUpdaterService devRolePropUpdaterService;
    @Autowired private DevAMRCreationService devAMRCreationService;
    @Autowired private DevCapControlCreationService devCapControlCreationService;
    @Autowired private DevStarsCreationService devStarsCreationService;
    private DevDbSetupTask devDbSetupTask;

    @Override
    @Transactional
    public synchronized void executeFullDatabasePopulation(DevDbSetupTask dbSetupTask) {
        if (devDbSetupTask != null && devDbSetupTask.isRunning()) {
            throw new RuntimeException("Already executing database population...");
        }
        doPopulate(dbSetupTask);
    }

    @Override
    public DevDbSetupTask getExecuting() {
        return devDbSetupTask;
    }

    private void doPopulate(DevDbSetupTask dbSetupTask) {
        try {
            devDbSetupTask = dbSetupTask;
            devDbSetupTask.setRunning(true);
            devRolePropUpdaterService.executeSetup(devDbSetupTask.getDevRoleProperties());
            devAMRCreationService.executeSetup(devDbSetupTask.getDevAMR());
            devCapControlCreationService.executeSetup(devDbSetupTask.getDevCapControl());
            devStarsCreationService.executeSetup(devDbSetupTask.getDevStars());
        } catch (Exception e) {
            devDbSetupTask.setHasRun(false);
            devDbSetupTask.setRunning(false);
            throw new RuntimeException(e);
        }
        devDbSetupTask.setHasRun(true);
        devDbSetupTask.setRunning(false);
    }
}
