package com.cannontech.servlet;

import javax.servlet.ServletContextEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.spring.YukonSpringHook;

public class RfnPerformanceVerificationListener extends ErrorAwareContextListener {
    @Override
    public void doContextInitialized(ServletContextEvent sce) {
        RfnPerformanceVerificationService service = YukonSpringHook.getBean(RfnPerformanceVerificationService.class);
        CTILogger.info("Attempting to start a task for performance verification messaging.");
        service.schedulePerformanceVerificationMessaging();
    }
}
