package com.cannontech.sensus;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.spring.YukonSpringHook;

public class SensusYukonMain {
    
    public static void main(String[] args) {
        // see if a log name was passed in
        String logName = System.getProperty("cti.app.name");
        if (logName == null) {
            System.setProperty("cti.app.name", "SensusFault");
        }
        
        final Logger log = YukonLogManager.getLogger(SensusServer.class);
        log.info("Sensus starting...");      

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception in " + t, e);
            }
        });

        try {
            String mainContextString[] = new String[0];
            if (args.length == 0) {
                mainContextString = new String[]{"sensusContext.xml"};
            } else if (args.length == 1) {
                mainContextString = args;
            }
            
            // load the parent context
            YukonSpringHook.setDefaultContext("com.cannontech.context.sensus-jms");
            ApplicationContext parentContext = YukonSpringHook.getContext();
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(mainContextString, parentContext);
            
            SensusServer sensusMain = (SensusServer) context.getBean("sensusMain");
            sensusMain.startServer();
        } catch (Exception e) {
            log.warn("Caught exception while starting SensusMain", e);
        }
    }
}
