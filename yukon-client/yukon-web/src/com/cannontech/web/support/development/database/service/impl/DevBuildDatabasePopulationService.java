package com.cannontech.web.support.development.database.service.impl;


import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

public class DevBuildDatabasePopulationService {
    
    @Autowired private static DevDatabasePopulationService devDatabasePopulationService;
    
    public static void main(String[] args) {
        //devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);
        devDatabasePopulationService.executeFullDatabasePopulation(new DevDbSetupTask());
    }

    
    
}
