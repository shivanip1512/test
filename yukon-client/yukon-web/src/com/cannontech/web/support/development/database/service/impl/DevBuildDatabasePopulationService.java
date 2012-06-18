package com.cannontech.web.support.development.database.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.development.BulkFakePointInjectionDto;
import com.cannontech.development.BulkPointDataInjectionService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.service.DevDatabasePopulationService;

public class DevBuildDatabasePopulationService {

    private static DevDatabasePopulationService devDatabasePopulationService;
    private static Logger log = YukonLogManager.getLogger(DevBuildDatabasePopulationService.class);
    private static BulkPointDataInjectionService bulkPointDataInjectionService;
    
    public static void main(String[] args) {
        try {
            YukonSpringHook.setDefaultContext(YukonSpringHook.WEB_BEAN_FACTORY_KEY);
            devDatabasePopulationService = YukonSpringHook.getBean(DevDatabasePopulationService.class);
            bulkPointDataInjectionService = YukonSpringHook.getBean(BulkPointDataInjectionService.class);

            // Setup task 
            DevDbSetupTask task = new DevDbSetupTask();
            List<DevPaoType> meters = task.getDevAMR().getMeterTypes();
            //Select all meter types
            for (DevPaoType dpt : meters) {
                dpt.setCreate(true);
            }
            task.getDevAMR().setMeterTypes(meters);
            task.getDevAMR().setNumAdditionalMeters(2);
            task.getDevStars().setNewEnergyCompanyName("Cooper EC");

            // Execute database population
            devDatabasePopulationService.executeFullDatabasePopulation(task);

            BulkFakePointInjectionDto bulkInjection = new BulkFakePointInjectionDto();
            bulkInjection.setAttribute(BuiltInAttribute.USAGE);
            bulkInjection.setIncremental(true);
            bulkInjection.setValueLow(2);
            bulkInjection.setValueHigh(4);
            bulkInjection.setAlgorithm("normal");

            // Execute bulk point injection
            bulkPointDataInjectionService.excecuteInjection(bulkInjection);
        } catch (Exception e) {
            log.warn("An Exception was thrown during database population. Database population may not have successfully finished. ",e);
        } finally {
            YukonSpringHook.shutdownContext();
            System.exit(0);
        }
    }
}
