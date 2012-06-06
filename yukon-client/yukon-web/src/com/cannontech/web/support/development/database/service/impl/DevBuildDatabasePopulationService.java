package com.cannontech.web.support.development.database.service.impl;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

public class DevBuildDatabasePopulationService {
    
    private static DevDatabasePopulationService devDatabasePopulationService;
    
    public static void main(String[] args) {
        YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
        devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);
        devDatabasePopulationService.executeFullDatabasePopulation(new DevDbSetupTask());
        YukonSpringHook.shutdownContext();
        System.exit(0);
    }
}
