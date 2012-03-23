/**
 * 
 */
package com.cannontech.selenium.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO: At the moment this class provide the access to the properties file, but 
 * I would like to build a singleton class that access all the properties 
 * from the yukon application. SolventSeleniumTestConfig is start for that.
 * 
 * @author anuradha.uduwage
 *
 */
public class SeleniumDefaultProperties {
	
	private static final Logger log = Logger.getLogger(SeleniumDefaultProperties.class.getName());

	private static final String SELENIUM_PROP_FILE = "seleniumdefault.properties";
	private static final String SELENIUM_IE_PROP = "seleniumdefault.ie.properties";
	
	private static SeleniumDefaultProperties instance = null;
	
	public static SeleniumDefaultProperties getClassInstance() {
		if(instance == null)
			instance = new SeleniumDefaultProperties();
		return instance;
	}

	public static String getResourceAsStream(String defaultProp) {
		String defaultPropValue = null;
		//String keys = null;
		try {
			InputStream inputStream = SeleniumDefaultProperties.class.getClassLoader().getResourceAsStream(SELENIUM_PROP_FILE);
			Properties properties = new Properties();
			//load the input stream using properties.
			properties.load(inputStream);
			defaultPropValue = properties.getProperty(defaultProp);
			
		}catch (IOException e) {
			log.error("Something wrong with .properties file, check the location.", e);
		}
		return defaultPropValue;
	}
	
	/**
	 * Returns the value from the selenium property file, and uses defaultValue if the value is empty.
	 */
	public static String getResourceAsStream(String defaultProp, String defaultValue) {
	    String propValue = getResourceAsStream(defaultProp);
	    if (StringUtils.isBlank(propValue)) {
	        return defaultValue;
	    }
        return propValue;
    }
	
	/**
	 * If the code is getting executed on actual server it will user the server name as the 
	 * default url for selenium test execution.<br>
	 * During development developer should set the url in seleniumdefault.properties file.
	 * @return url url string of the host.
	 */
	protected static String getBaseHREF() {
		
		java.net.InetAddress localMachine = null;
		String defaultHost = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			defaultHost = localMachine.getHostName();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String url = null;
		String host = getResourceAsStream("default.auth.url");
		if(host.equalsIgnoreCase(""))
			url = "http://" + defaultHost + ":8080/";
		else 
			url = getResourceAsStream("default.auth.url");
		return url;
	}
}
