package com.cannontech.sensus;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.spring.YukonSpringHook;

public class SensusMain {
    public static void main(String[] args) {
        System.setProperty("cti.app.name", "SensusFault");
        Logger log = YukonLogManager.getLogger(SensusMain.class);
        try {
            CTILogger.info("Sensus starting...");
            String mainContextString[] = new String[0];
            if (args.length == 0) {
                mainContextString = new String[]{"sensusContext.xml"};
            } else if (args.length == 1) {
                mainContextString = args;
            }
            
            // load the parent context
            ApplicationContext context = YukonSpringHook.getContext("com.cannontech.context.sensus-jms");
            new FileSystemXmlApplicationContext(mainContextString, context);
            log.info("Sensus main started");

        } catch (Exception e) {
            log.error("Unable to start " + SensusMain.class, e);
        }
    }
}
