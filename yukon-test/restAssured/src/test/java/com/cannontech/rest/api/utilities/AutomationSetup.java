package com.cannontech.rest.api.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.baseURI;
import io.restassured.config.LogConfig;

public class AutomationSetup {
    private static BasicConfiguration basicConfiguration = BasicConfiguration.getInstance();

    @Test
    public static void initialAutomationSetup() throws IOException {

        setBasicConfiguration();
        setupRestAssuredConfiguration();
    }

    public static void setBasicConfiguration() throws IOException {

        String rootLogDirectory = System.getProperty("user.dir") + "\\Logs\\";
        
        // Log4j configuration
        basicConfiguration.setlog4jPath(
            System.getProperty("user.dir") + File.separatorChar + "src" + File.separatorChar+ "main" + File.separatorChar+ "resources" + File.separatorChar + "log4j2.properties");

        Date Date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDate = dateFormat.format(Date);
        String testLog = rootLogDirectory.concat(currentDate).concat("RESTAssured.logs");

        System.setProperty("logDir", testLog);
        basicConfiguration.setTestLogPath(testLog);
        basicConfiguration.setconfigPropertyFilePath(System.getProperty("user.dir") + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar+ "resources" + File.separatorChar + "configuration.properties");
        basicConfiguration.setEndpoint(
            ReadPropertyFile.getPropertyValue("baseURI", basicConfiguration.getconfigPropertyFilePath()));
        
        ReadPropertyFile.updateProperty("testLogPath", testLog, basicConfiguration.getconfigPropertyFilePath());

        Log.info("Base URI is : " + basicConfiguration.getEndpoint());
        Log.info("Test Log Path : " + basicConfiguration.getTestLogPath());
        Log.info("Log4j properties file path : " + basicConfiguration.getlog4jPath());
    }

    public static void setupRestAssuredConfiguration() throws IOException {

        // RestAssured URI and port - this will be common for all rest

        baseURI = basicConfiguration.getEndpoint();
        // RestAssured.port=basicConfiguration.getApplicationPort();

        // RestAssured log configuration
        String testLogPath = basicConfiguration.getTestLogPath();

        PrintStream fileOutPutStream = new PrintStream(new FileOutputStream(testLogPath, true));
        RestAssured.config = RestAssured.config().logConfig(new LogConfig().defaultStream(fileOutPutStream));

    }

}
