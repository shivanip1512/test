package com.cannontech.web.updater.development;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dev.DevDbSetupTask;
import com.cannontech.web.dev.database.service.DevDatabasePopulationService;
import com.cannontech.web.updater.UpdateBackingService;

public class DevBackingService implements UpdateBackingService {
    private DevDatabasePopulationService devDatabasePopulationService;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        DevExecutionUpdaterTypeEnum updaterType = DevExecutionUpdaterTypeEnum.valueOf(identifier);
        DevDbSetupTask devDbSetupTask = devDatabasePopulationService.getExecuting();
        Object resultObj = updaterType.getValue(devDbSetupTask);
        String resultStr = String.valueOf(resultObj);
        return resultStr;
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    @PostConstruct
    public void init() throws Exception {
    }
    @Autowired
    public void setDevDatabasePopulationService(DevDatabasePopulationService devDatabasePopulationService) {
        this.devDatabasePopulationService = devDatabasePopulationService;
    }
}