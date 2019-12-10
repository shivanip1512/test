package com.cannontech.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cannontech.cloud.service.CloudService;

/**
 * This is entry point of the application (service), spring boot services starts here.
 * This in turn will call start of cloud services.
 * JMS Listener and publishers will be started automatically.
 */
@SpringBootApplication(scanBasePackages = "com.cannontech")
public class BootStrapService implements CommandLineRunner {

    Logger log = (Logger) LogManager.getLogger(BootStrapService.class);
    @Autowired CloudService cloudServices;

    public static void main(String[] args) {
        SpringApplication.run(BootStrapService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Cloud Service");
        cloudServices.startServices();
    }

}
