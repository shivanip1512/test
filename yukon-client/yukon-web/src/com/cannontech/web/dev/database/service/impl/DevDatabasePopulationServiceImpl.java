package com.cannontech.web.dev.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.development.service.DevAMRCreationService;
import com.cannontech.web.dev.DevDbSetupTask;
import com.cannontech.web.dev.database.service.DevCapControlCreationService;
import com.cannontech.web.dev.database.service.DevDatabasePopulationService;
import com.cannontech.web.dev.database.service.DevRolePropUpdaterService;
import com.cannontech.web.dev.database.service.DevStarsCreationService;

public class DevDatabasePopulationServiceImpl implements DevDatabasePopulationService {
    @Autowired private DevRolePropUpdaterService devRolePropUpdaterService;
    @Autowired private DevAMRCreationService devAMRCreationService;
    @Autowired private DevCapControlCreationService devCapControlCreationService;
    @Autowired private DevStarsCreationService devStarsCreationService;
    private DevDbSetupTask devDbSetupTask;

    @Override
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
            devStarsCreationService.executeEnergyCompanyCreation(devDbSetupTask.getDevStars());
            devStarsCreationService.executeStarsAccountCreation(devDbSetupTask.getDevStars());
            devRolePropUpdaterService.executeSetup(devDbSetupTask.getDevRoleProperties());
            devAMRCreationService.executeSetup(devDbSetupTask.getDevAMR());
            devCapControlCreationService.executeSetup(devDbSetupTask.getDevCapControl());
        } catch (Exception e) {
            devDbSetupTask.setHasRun(false);
            devDbSetupTask.setRunning(false);
            throw new RuntimeException(e);
        }
        devDbSetupTask.setHasRun(true);
        devDbSetupTask.setRunning(false);
    }
}
