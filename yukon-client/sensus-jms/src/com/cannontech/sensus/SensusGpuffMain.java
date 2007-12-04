package com.cannontech.sensus;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SensusGpuffMain {
    
    public static void main(String[] args) {
        Logger log = Logger.getLogger(SensusServer.class);

        

        try {
            if (args.length != 2) {
                System.err.println("Must specify context.xml and loggin.xml files");
                System.exit(1);
            }

            DOMConfigurator.configure(args[1]);

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
