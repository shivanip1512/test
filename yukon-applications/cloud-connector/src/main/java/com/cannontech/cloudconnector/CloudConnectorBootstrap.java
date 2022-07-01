package com.cannontech.cloudconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CloudConnectorBootstrap implements CommandLineRunner {

    @Autowired ApplicationContext ctx;
    @Autowired CloudConnector CloudConnector;

    public static void main(String[] args) {
        SpringApplication.run(CloudConnectorBootstrap.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CloudConnector.start();
    }

}
