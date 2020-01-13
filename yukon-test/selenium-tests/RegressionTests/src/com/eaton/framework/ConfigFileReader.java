package com.eaton.framework;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

    private Properties properties;
    private static final String FILE_PATH = "configs//Config.properties";

    public ConfigFileReader() throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(FILE_PATH));
            this.properties = new Properties();

            try {
                this.properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + FILE_PATH);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public String getDriverPath() {

        String driverPath = this.properties.getProperty("driverPath");

        if (driverPath != null)
            return driverPath;

        throw new RuntimeException("driverPath is not specified in the Configuration.properties file.");
    }

    public String getApplicationUrl() {

        String url = this.properties.getProperty("url");

        if (url != null)
            return url;

        throw new RuntimeException("url is not specified in the Configuration.properties file.");
    }

    public String getBrowser() {

        String browser = this.properties.getProperty("browser");

        if (browser != null)
            return browser;

        throw new RuntimeException("browser is not specified in the Configuration.properties file.");
    }

    public String getUseRemoteDriver() {

        String useRemoteDriver = this.properties.getProperty("useRemoteDriver");

        if (useRemoteDriver != null)
            return useRemoteDriver;

        throw new RuntimeException("useRemoteDriver is not specified in the Configuration.properties file.");
    }

    public String getRunHeadless() {

        String runHeadless = this.properties.getProperty("runHeadless");

        if (runHeadless != null)
            return runHeadless;

        throw new RuntimeException("runHeadless is not specified in the Configuration.properties file.");
    }
}
