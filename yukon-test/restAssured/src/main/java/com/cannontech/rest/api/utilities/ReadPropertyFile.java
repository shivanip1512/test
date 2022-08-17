package com.cannontech.rest.api.utilities;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ReadPropertyFile {

    /**
     * @param propertyName - name of the property to be read from property file
     * @param propertiesFilePath - relative folder path ( w.r.t to root project directory) of properties file.
     * @return
     * @throws IOException
     */
    public static String getPropertyValue(String propertyName, String propertiesFilePath) throws IOException {
        Properties obj = new Properties();
        FileReader objfile = new FileReader(propertiesFilePath);
        // Pass object reference objfile to load method of Properties object.
        obj.load(objfile);
        objfile.close();
        String propertyValue = obj.getProperty(propertyName);
        return propertyValue;
    }

    /**
     * @param propertyName
     * @param propertiesFilePath - relative folder path ( w.r.t to root project directory) of properties file.
     * @return String
     * @throws IOException
     */
    public static String updateProperty(String propertyName, String propertyValue, String propertiesFilePath)
            throws IOException {

        FileReader in = new FileReader(propertiesFilePath);
        Properties props = new Properties();
        props.load(in);
        in.close();

        FileWriter out = new FileWriter(propertiesFilePath);
        props.setProperty(propertyName, propertyValue);
        props.store(out, null);
        out.close();

        Properties obj = new Properties();
        FileReader objfile = new FileReader(propertiesFilePath);
        // Pass object reference objfile to load method of Properties object.
        obj.load(objfile);
        String UpdatedPropertyValue = obj.getProperty(propertyName);
        objfile.close();
        return UpdatedPropertyValue;

    }
}
