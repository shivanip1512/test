package com.cannontech.sensus;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;

public class SensusGpuffMain {
    
    public static void main(String[] args) {
        BootstrapUtils.setApplicationName(ApplicationId.SENSUS_GPUFF_DECODE);

        final Logger log = YukonLogManager.getLogger(SensusServer.class);

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception in " + t, e);
            }
        });

        try {
            if (args.length != 2) {
                System.err.println("Must specify context.xml and logging.xml files");
                System.exit(1);
            }

          /*  DOMConfigurator.configure(args[1]);*/

            log.info("Sensus-GPUFF starting with: " + Arrays.toString(args));

            // load the parent context
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(args[0]);
            
            SensusServer sensusMain = (SensusServer) context.getBean("sensusMain");
            sensusMain.startServer();
        } catch (Exception e) {
            log.warn("Caught exception while starting SensusMain", e);
        }
    }
}
