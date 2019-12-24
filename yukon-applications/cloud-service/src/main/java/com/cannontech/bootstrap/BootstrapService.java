package com.cannontech.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import com.cannontech.cloud.service.CloudService;

/**
 * This is entry point of the application (service), spring boot services starts here.
 * This in turn will call start of cloud services.
 * JMS Listener and publishers will be started automatically.
 */
@SpringBootApplication(scanBasePackages = "com.cannontech")
@EnableJms
public class BootstrapService implements CommandLineRunner {

    static {
        System.setProperty("logPath", BootstrapServiceUtils.getPath());
    }
    Logger log = (Logger) LogManager.getLogger(BootstrapService.class);
    @Autowired CloudService cloudServices;

    public static void main(String[] args) {
        SpringApplication.run(BootstrapService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Cloud Service");
        cloudServices.startServices();
    }

}
