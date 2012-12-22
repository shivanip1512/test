package com.cannontech.servlet;

import javax.servlet.ServletContextEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.spring.YukonSpringHook;

public class MaintenanceContextListener extends ErrorAwareContextListener{

    @Override
    public void doContextInitialized(ServletContextEvent sce) {
        
        TemporaryDeviceGroupService temporaryDeviceGroupService = YukonSpringHook.getBean("temporaryDeviceGroupService", TemporaryDeviceGroupService.class);
        CTILogger.info("Attempting to delete temporary device groups on startup.");
        temporaryDeviceGroupService.deleteTemporaryGroups();        
    }

}
