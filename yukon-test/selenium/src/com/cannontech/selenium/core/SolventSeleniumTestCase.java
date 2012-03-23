/**
 * 
 */
package com.cannontech.selenium.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cannontech.selenium.core.input.DataFileFinder;
import com.cannontech.selenium.core.input.InvalidDataException;
import com.cannontech.selenium.core.input.MissingDataFileException;
import com.cannontech.selenium.core.input.XMLDataFileDigester;
import com.thoughtworks.selenium.SeleniumException;

/**
 * All the JUnit tests should extend this abstract class to get an handle to the Selenium
 * server.<br>
 * Class looks for the default xml input file as a setup process and extract the input params.<br>
 * Speed of the tests are controlled from this class.
 * 
 * @author anuradha.uduwage
 *
 */
public abstract class SolventSeleniumTestCase {
	
	private static final Logger log = Logger.getLogger(SolventSeleniumTestCase.class.getName());
	private boolean dataFileFound = false;
	
	//this arraylist holds the selenium sessions, at end of the day close them down.
	private final ArrayList<SolventSelenium> sessions = new ArrayList<SolventSelenium>();
	
	//hold input params because all the data file digesters return hashmaps.
	private HashMap<String, Object> inputParams;
	
	//setting up custom URL inside the test case to override test running against local host.
	private String customURL;
	
	/**
	 * Load input data from input file for the test. if an input file is not found or not 
	 * given by the test method will assumed that there is no input required for the given test.
	 */
	@Before
	public void setUp() {
		try {
			inputParams = new HashMap<String, Object>();
			
			//read input parameters if input file is found.
			InputStream in = DataFileFinder.getDataInputFileAsStream(this);
			if (in != null) {
				dataFileFound = true;
				log.info("Found test input file....doing some data digesting... hang on..");
				XMLDataFileDigester digester = new XMLDataFileDigester(in);
				digester.parseInput(inputParams);
			}
		}catch (Exception e) {
			log.error("Something went wrong while parsing input file", e);
		}
	}

	/**
	 * Validate the the dataFileFound flag. if it is set to false, then exception will be thrown.
     * This method is used to validate that an Input file was actually parsed, before trying to retrieve parameters.
     * This avoids confusion if the user is trying to retrieve parameters from a file that doesn't exist,
     * or is in the wrong location, or has the wrong name.
	 */
	protected void validateInputData() {
		if(!dataFileFound) {
			log.error("Unable to retrieve requested input parameter, no input files found for the test");
			throw new MissingDataFileException("Unable to retrieve input parameter. no input files" + 
					" found for the test");
		}
	}
	
	/**
	 * If there is a value defined for single-value parameter the method will 
	 * retrieve that value. If matching parameter not found, or if the file doesn't 
	 * exists, the method will return null.
	 * @param name name of the single-value parameter that to be retrieve.
	 * @return Value of the parameter, null if the parameter was not found.
	 */
	protected String getParamString(String name) {
		validateInputData();
		if(inputParams.get(name) == null) {
			throw new InvalidDataException("Input parameter \"" + name + "\" not found)");
		}
		return (String)inputParams.get(name);
	}
	
	/**
	 * If there are values defined for a multi-value parameter the method will 
	 * retrieve those values. If matching parameters not found, or if its a single-value 
	 * method will inform that.
	 * @param name The name of the multi-value parameter.
	 * @return Method returns an array of values defined for the specified parameter, or null, 
	 * or if its a single-value.
	 */
	protected String[] getParamStrings(String name) {
		validateInputData();
		if(inputParams.get(name) == null) {
			throw new InvalidDataException("Input parameter \"" + name + "\" not found)");
		}
		String[] values = null;
		try {
			values = (String[])inputParams.get(name);
		}catch(ClassCastException ex) {
			throw new InvalidDataException("Input parameter \"" + name + "\" is not a multi-value parameter. " +
					"Use the method that accept single parameter getParamString())", ex);
		}
		return values;
	}
	
	/**
	 * Method set the custom url inside the test case.
	 * @param customURL the customeURL to set
	 */
	protected void setCustomURL(String customURL) {
		this.customURL = customURL;
	}	
	
	/**
	 * Call this method when test are completed to force all the Selenium session to be shutdown. 
	 * This prevents hanging browser windows cause by failures during a test.
	 */
	@After
	public void tearDown() {
		boolean autoClose = Boolean.parseBoolean(SeleniumDefaultProperties.getResourceAsStream("default.autoclose"));
		
		if(autoClose) {
			for (SolventSelenium session:sessions) {
				//since we really don't know what sessions are all ready close we should catch the exception
				try {
					session.stop();
				}catch(Throwable t) {
					//not the best way since brute force, logs will help
					log.debug("Error thrown attempt of closing session ('not a big deal ignore') : \n" + t.getMessage());
				}
			}
		}
	}
	
	/**
	 * Returns a Selenium session after an authentication action using the default values in .properties 
	 * file for browser.command, username, and password.
	 * @return
	 */
	protected SolventSelenium getAuthenticatedSeleniumSession() {
		//changing to get it from out side.
		return this.getConfiguredSeleniumSession(this.getBrowserDefaultCommand());
	}
	
	public SolventSelenium getConfiguredSeleniumSession() {
		return getConfiguredSeleniumSession(getBrowserDefaultCommand());
	}
	
	public SolventSelenium getConfiguredSeleniumSession(String browserCommand) {
		// These values should be defined in .properties file. but incase if its not define
		String host = SeleniumDefaultProperties.getResourceAsStream("default.selenium.host", "localhost");
		String port = SeleniumDefaultProperties.getResourceAsStream("default.selenium.port", "4444"); 
		String testHostURL = SeleniumDefaultProperties.getResourceAsStream("default.auth.url", SeleniumDefaultProperties.getBaseHREF());

		// Check to see if the customUrl has been set and use that if it does.
		if(this.customURL != null) {
			testHostURL = this.customURL;
		}
		
		// Create selenium instance and needed properties
		SolventHttpCommandProcessor commandProcessor = 
			new SolventHttpCommandProcessor(host, Integer.parseInt(port), browserCommand, testHostURL);
		SolventSelenium selenium = new SolventSelenium(commandProcessor);
		
		// Start up the selenium instance and open to the test host url.
		selenium.start();
		selenium.open(testHostURL);
		selenium.setTimeout(SeleniumDefaultProperties.getResourceAsStream("default.pageload.timeout"));
		
		// Use a different XPath library for IE;
		if (isIETheDefaultBrowser()) {
		    selenium.useXpathLibrary("javascript-xpath");
		}
		
		sessions.add(selenium);
		return selenium;
	}
	
	/**
	 * Method to start a new selenium session. use this when you don't want to start with the test 
	 * with any specifict Solvent and do not need to override any of the defualt session configurations.
	 * @see #start(AbstractSolvent)
	 * @return SolventSelenium = the Selenium session that was freshly created.
	 */
	public SolventSelenium start() {
		try {
			start(getAuthenticatedSeleniumSession());
		}catch (Exception e) {
			log.error("Selenium session is null, " + e);
		}
		return SeleniumSession.get();
	}
	/**
	 * This method uses defualt configuration values to start a new session and accepts a 
	 * solvent to start the test with.
	 * @param <X>
	 * @param sol The solvent to start the test with.
	 * @return Solvent that was passed to start with.
	 */
	public <X extends AbstractSolvent> X start(X sol) {
		return start(sol);
	}
	/**
	 * Method that accepts the selenium session object. This allows the user to create the 
	 * start session to their preference, without using the default defined properties.
	 * @param selenium The Selenium session to use for this test.
	 */
	public void start(SolventSelenium selenium) {
		SeleniumSession.set(selenium);
	}
	/**
	 * Starts the selenium test. Calling this method will cause the browser to open and the test 
	 * to begin starting with the Solvent that is passed in as the solventSelenium parameter.
	 * @param <X>
	 * @param solventSelenium An authenticated selenium session
	 * @param sol The solvent that will be used to begin the test.
	 * @return solvent.
	 */
	public <X extends AbstractSolvent> X start(SolventSelenium solventSelenium, X sol) {
		sol.setSeleniumDriver(solventSelenium);
		sol.prepare();
		return sol;
	}

	/**
	 * Retrieves configuration browser command where selenium test should run. 
	 * Method reads the system property value passed by the command line and get the correct browser value.
	 * @throws SolventConfigException if not found
	 * @return the value of the selenium.browser.startCommand
	 */
	public String getBrowserDefaultCommand() {
		String defaultBrowser = System.getProperty("default.browser");
		String browswerCommand = null;
		if(defaultBrowser != null) {
			if(defaultBrowser.equalsIgnoreCase("firefox")) {
				browswerCommand = SeleniumDefaultProperties.getResourceAsStream("default.browser.command");
				if(browswerCommand.equals(""))
					handleMissingConfigProperties(SeleniumDefaultProperties.getResourceAsStream("default.browser.command"));
			}
			else if(defaultBrowser.equalsIgnoreCase("ie")){
				browswerCommand = SeleniumDefaultProperties.getResourceAsStream("default.ie.command");
				if(browswerCommand.equals(""))
					handleMissingConfigProperties(SeleniumDefaultProperties.getResourceAsStream("default.ie.command"));			
			}
		}
		else {
			browswerCommand = SeleniumDefaultProperties.getResourceAsStream("default.browser.command");
		}
		return browswerCommand;
	}
	
	private boolean isIETheDefaultBrowser(){
	    String browserType = SeleniumDefaultProperties.getResourceAsStream("default.browser.command");
	    String ieBrowserType = SeleniumDefaultProperties.getResourceAsStream("default.ie.command");
	
	    if(browserType.equalsIgnoreCase(ieBrowserType)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Method that handle missing Run Configuration properties.
	 * @param propname name of the property that ism missing
	 * @throws SolventConfigException
	 */
	public void handleMissingConfigProperties(String propname) {
		String msg = "Missing requried configuration property \"" + propname + 
			"\". check the properties file and try again.";
		log.error(msg);
		throw new SolventConfigException(msg);
	}
	
	/**
	 * Method 10 seconds and during that process it will catch any exception.
	 */
	@Deprecated
	public void waitTenSeconds(){
	    try {
			Thread.sleep(10000);
		}catch(InterruptedException ex) {
			log.error("Please wait its interrupted", ex);
			throw new SeleniumException("wait() interrupted! \n" + ex.getMessage(), ex);
		}
	}
	
	/**
	 * Method wait 5 seconds and during that process it will catch any exception that get thrown.
	 */
	@Deprecated
	public void waitFiveSeconds() {
	    try {
			Thread.sleep(5000);
		}catch(InterruptedException ex) {
			log.error("Please wait its interrupted", ex);
			throw new SeleniumException("wait() interrupted! \n" + ex.getMessage(), ex);
		}		
	}
}
