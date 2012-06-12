package com.cannontech.web.support.development.database.service.impl;

import java.util.List;

import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

public class DevBuildDatabasePopulationService {

    private static DevDatabasePopulationService devDatabasePopulationService;

    public static void main(String[] args) {
        try {
            YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
            devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);

            // Setup task 
            DevDbSetupTask task = new DevDbSetupTask();
            List<DevPaoType> meters = task.getDevAMR().getMeterTypes();
            //Select all meter types
            for (DevPaoType dpt : meters) {
                dpt.setCreate(true);
            }
            task.getDevAMR().setMeterTypes(meters);
            task.getDevAMR().setNumAdditionalMeters(2);
            task.getDevStars().setCreateCooperEC(true);
            task.setBulkPointDataInject(true);

            // Execute database population
            devDatabasePopulationService.executeFullDatabasePopulation(task);
        } catch (Exception e) {
        } finally {
            YukonSpringHook.shutdownContext();
            System.exit(0);
        }
    }
}
