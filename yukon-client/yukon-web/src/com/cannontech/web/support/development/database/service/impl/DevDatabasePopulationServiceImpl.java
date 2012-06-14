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

    @Transactional
    public synchronized void executeFullDatabasePopulation(DevDbSetupTask dbSetupTask) {
        if (devDbSetupTask != null && devDbSetupTask.isRunning()) {
            throw new RuntimeException("Already executing database population...");
        }
        doPopulate(dbSetupTask);
    }

    public DevDbSetupTask getExecuting() {
        return devDbSetupTask;
    }

    public void cancelExecution() {
        devDbSetupTask.setCancelled(true);
    }

    private void doPopulate(DevDbSetupTask dbSetupTask) {
        try {
            devDbSetupTask = dbSetupTask;
            devDbSetupTask.setRunning(true);
            
            if (devDbSetupTask.isUpdateRoleProperties()) {
                devRolePropUpdaterService.createAll(devDbSetupTask);
            }
            if (devDbSetupTask.getDevAMR().isCreate()) {
                devAMRCreationService.createAll(devDbSetupTask);
            }
            if (devDbSetupTask.getDevCapControl().isCreate()) {
                devCapControlCreationService.createAll(devDbSetupTask);
            }
            if (devDbSetupTask.getDevStars().isCreate()) {
                devStarsCreationService.createAll(devDbSetupTask);
            }
        } catch (Exception e) {
            devDbSetupTask.setHasRun(false);
            devDbSetupTask.setRunning(false);
            throw new RuntimeException(e);
        }
        logExecutionDetails();
        devDbSetupTask.setHasRun(true);
        devDbSetupTask.setRunning(false);
    }
    
    private void logExecutionDetails() {
        devAMRCreationService.logFinalExecutionDetails(devDbSetupTask.getDevAMR());
        devCapControlCreationService.logFinalExecutionDetails(devDbSetupTask.getDevCapControl());
        devStarsCreationService.logFinalExecutionDetails(devDbSetupTask.getDevStars());
    }
}
