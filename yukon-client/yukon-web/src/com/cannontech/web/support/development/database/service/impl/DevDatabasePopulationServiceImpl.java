package com.cannontech.web.support.development.database.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.service.DevAMRCreationService;
import com.cannontech.web.support.development.database.service.DevCapControlCreationService;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;
import com.cannontech.web.support.development.database.service.DevRolePropUpdaterService;
import com.cannontech.web.support.development.database.service.DevStarsCreationService;

public class DevDatabasePopulationServiceImpl implements DevDatabasePopulationService {
    private DevAMRCreationService devAMRCreationService;
    private DevRolePropUpdaterService devRolePropUpdaterService;
    private DevCapControlCreationService devCapControlCreationService;
    private DevStarsCreationService devStarsCreationService;
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
            throw new RuntimeException(e.getMessage());
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

    @Autowired
    public void setDevRolePropUpdaterService(DevRolePropUpdaterService devRolePropUpdaterService) {
        this.devRolePropUpdaterService = devRolePropUpdaterService;
    }

    @Autowired
    public void setDevAMRCreationService(DevAMRCreationService devAMRCreationService) {
        this.devAMRCreationService = devAMRCreationService;
    }

    @Autowired
    public void setDevCapControlCreationService(DevCapControlCreationService devCapControlCreationService) {
        this.devCapControlCreationService = devCapControlCreationService;
    }

    @Autowired
    public void setDevStarsCreationService(DevStarsCreationService devStarsCreationService) {
        this.devStarsCreationService = devStarsCreationService;
    }
}
