/**
 * 
 */
package com.cannontech.selenium.core;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * SolventSelenium provide access to DefualtSelenium class by extending it.
 * Two constructors to accept instance of SolventHttpCommandProcessor and Default
 * command processor.
 * 
 * @author anuradha.uduwage
 *
 */
public class SolventSelenium extends DefaultSelenium {

	private static final Logger log = Logger.getLogger(SolventSelenium.class.getName());
	private static final Duration STANDARD_WAIT_DURATION = Duration.standardSeconds(5); 
	
	/**
	 * Constructor that accept customized instance of the command processor.
	 * @param processor
	 */
	public SolventSelenium(SolventHttpCommandProcessor processor) {
		super(processor);
	}
	
	/**
	 * Constructor that sets server startup values. 
	 * 
	 * @param serverHost host name of the server.
	 * @param serverPort address of the selenium port.
	 * @param browserStartCommand default browswer command.
	 * @param browserURL default browser url.
	 */
	public SolventSelenium(String serverHost, int serverPort, 
			String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}

	/**
	 * Simple pause method with a try/catch for the InterruptedException;
	 * only here to keep the try/catch block out of the test code.
	 * @param milliseconds
	 */
	public void pause(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		}catch(InterruptedException ex) {
			log.error("Please wait its interrupted", ex);
			throw new SeleniumException("pause() interrupted! \n" + ex.getMessage(), ex);
		}
	}
	
	/**
	 * Method used to wait for an element to appear on the page before continuing.
	 * @Deprecated You should use the one that uses waitForElement(String locator,Duration wait)
	 */
	@Deprecated
	public void waitForElement(String locator, int wait) {
		boolean present = isElementPresent(locator, 0);
		checkForHTTP500();
		while(!present && wait > 0) {
			pause(500);
			wait -= 500;
			present = isElementPresent(locator, 0);
		}
		if(!present) {
			String messageString = "Timeout while waiting for element " + locator + " to appear";
			log.error(messageString);
			throw new SeleniumException(messageString);
		}
	}

    /**
     * Method used to wait for an element to appear on the page before continuing.
     */
	public void waitForElement(String locator, Duration wait) {
	     boolean present = isElementPresent(locator, 0);
	     checkForHTTP500();
	     while(!present && wait.isLongerThan(Duration.ZERO)) {
	         pause(wait.getMillis());
	         wait = wait.minus(Duration.millis(500));
	         present = isElementPresent(locator, 0);
	     }
	     if(!present) {
	         String messageString = "Timeout while waiting for element " + locator + " to appear";
	         log.error(messageString);
	         throw new SeleniumException(messageString);
	     }
	}

	
	/**
	 * Method used to wait for an element to appear on the page before continuing.
	 */
	public void waitForElement(Locator locator, Duration wait) {
	    waitForElement(locator.generateLocatorString(), wait);
	}

	public String getText(Locator locator) {
	    return getText(locator.generateLocatorString());
	}
	
	
	
	/**
	 * Method used to wait for an element to disappear on the page before continuing.
	 */
	public void waitForElementRemoved(String locator, int wait) {
	    boolean present = isElementPresent(locator, 0);
	    checkForHTTP500();
	    while(present && wait > 0) {
	        pause(500);
	        wait -= 500;
	        present = isElementPresent(locator, 0);
	    }
	    if(present) {
	        String messageString = "Timeout while waiting for element " + locator + " to appear";
	        log.error(messageString);
	        throw new SeleniumException(messageString);
	    }
	}

	public void waitForElement(String locator) {
		waitForElement(locator, Duration.standardSeconds(5));
	}
	
	public void waitForElement(Locator locator) {
	    waitForElement(locator.generateLocatorString());
	}
	
	/**
	 * Given the location method will check the if the locator exists.
	 * @param locator
	 * @param wait
	 * @return
	 */
	public boolean isElementPresent(String locator, long wait) {
		boolean present = super.isElementPresent(locator);
		while(!present && wait > 0) {
			pause(500);
			wait -= 500;
			present = super.isElementPresent(locator);
		}
		return present;
	}
	
    /**
     * Given the location method will check the if the locator exists.
     * @param locator
     * @param wait
     * @return
     */
    public boolean isElementPresent(String locator, Duration wait) {
        boolean present = super.isElementPresent(locator);
        while(!present && wait.isLongerThan(Duration.ZERO)) {
            pause(wait.getMillis());
            wait = wait.minus(500);
            present = super.isElementPresent(locator);
        }
        return present;
    }

    public boolean isElementPresent(Locator locator, Duration wait) {
        return isElementPresent(locator.generateLocatorString(), wait);
    }
	
	/**
	 * Override the default method to add more consistent wait time.
	 * @param locator element locator.
	 */
	@Override
	public boolean isElementPresent(String locator) {
		return isElementPresent(locator, Duration.standardSeconds(3));
	}

    public boolean isElementPresent(Locator locator) {
        return isElementPresent(locator.generateLocatorString());
    }

    /**
     * Extends the functionality of Selenium's isVisible to use locators.
     */
    public boolean isVisible(Locator locator) {
        return isVisible(locator.generateLocatorString());
    }

    /**
     * Checks to see if a locator is not visible.
     */
    public boolean isInvisible(Locator locator) {
        return !isVisible(locator.generateLocatorString());
    }

	/**
	 * Override the default type method to add more consistent wait time throughout<br>
	 * the selenium test suite.
	 * @param locator element locator.
	 * @param value value to be typed.
	 */
	@Override
	public void type(String locator, String value) {
		waitForElement(locator);
		super.type(locator, value);
	}

	public void type(Locator locator, String value) {
	    type(locator.generateLocatorString(), value);
	}
	
	public void keyPress(Locator locator, String keySequence){
	    keyPress(locator.generateLocatorString(), keySequence);
	}
	
	public void keyDown(Locator locator, String keySequence){
	    keyDown(locator.generateLocatorString(), keySequence);
	}
	
	/**
	 * Override the default click method to add more consistent wait time throughout <br>
	 * the Selenium test suite.
	 * @param locator element to be click.
	 */
	@Override
	public void click(String locator) {
		waitForElement(locator);
		super.click(locator);
	}

	public void click(Locator locator) {
	    click(locator.generateLocatorString());
	}
	
	/**
	 * Helper click method that avoids the wait and click. 
	 * @param locator element to be click.
	 */
	public void clickWithoutWait(String locator) {
		try {
			super.click(locator);
			boolean present = isElementPresent(locator);
			if(!present)
				super.refresh();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Override the default right click mouse down action method to slow down while checking <br>
	 * for the availability of the element to be click.
	 * @param locator element to be click by mouse down action.
	 */
	@Override
	public void mouseDown(String locator) {
		boolean present = isElementPresent(locator);
		if(present)
			super.mouseDown(locator);
		else
			throw new SolventSeleniumException("Element is not available to click at " + locator);
	}
	
	/**
	 * Override the default right click mouse up action method to slow down while checking <br>
	 * for the availability of the element to be click.
	 * @param locator element to be click by mouse up action.
	 */
	@Override
	public void mouseUp(String locator) {
		boolean present = isElementPresent(locator);
		if(present)
			super.mouseUp(locator);
		else
			throw new SolventSeleniumException("Element is not available to click at " + locator);		
	}
	
	/**
	 * isElementPresent just verifies the element exists in the DOM
	 * this API will also verify the element is not hidden.
	 * @param locator
	 * @param wait
	 * @return
	 */
	public boolean isElementPresentAndVisible(String locator, int wait) {
		if(isElementPresent(locator, wait)) {
			return super.isVisible(locator);
		}
		return false;
	}
	
	/**
     * isElementPresent just verifies the element exists in the DOM
     * this API will also verify the element is not hidden.
     * @param locator
     * @param wait
     * @return
     */
    public boolean isElementPresentAndVisible(String locator, Duration wait) {
        if(isElementPresent(locator, wait)) {
            return super.isVisible(locator);
        }
        return false;
    }
	
	/**
	 * Waits for the element to become visible (i.e: not hidden)
	 * @param locator - The elements locator
	 * @param wait - Amount of time in ms to wait.
	 * @return
	 */
	public boolean waitForElementToBecomeVisible(String locator, long wait) {
		while(!isVisible(locator)) {
		    
		    if (wait < 0) {
		        throw new SolventSeleniumException("Timeout while waiting for element " + locator +  " to become visible");
		    }
		    
			pause(100);
			wait -= 100;
		}

		return isVisible(locator);
	}

	/**
	 * This method waits for an element to become visible and uses the STANDARD_WAIT_DURATION as a wait duration.
	 */
	public boolean waitForElementToBecomeVisible(Locator locator) {
        return waitForElementToBecomeVisible(locator, STANDARD_WAIT_DURATION);
    }

	public boolean waitForElementToBecomeVisible(Locator locator, Duration wait) {
        return waitForElementToBecomeVisible(locator.generateLocatorString(), wait.getMillis());
    }
	
   /**
     * Waits for the element to become invisible (i.e: not hidden)
     * @param locator - The elements locator
     * @param wait - Amount of time in ms to wait.
     * @throws SolventSeleniumException - 
     */
    public void waitForElementToBecomeInvisible(Locator locator, Duration wait) {
        while(wait.isLongerThan(Duration.ZERO)) {
            if (isInvisible(locator)) {
                return;
            }

            pause(500);
            wait = wait.minus(Duration.millis(500));
        }

        throw new SolventSeleniumException("Timeout while waiting for element " + locator +  " to become visible");
    }
    
    public void waitForElementToBecomeInvisible(Locator locator) {
        waitForElementToBecomeInvisible(locator, STANDARD_WAIT_DURATION);
    }
	
	/**
	 * Overrides the waitForPageToLoad in DefualtSelenium, method will check for errors 
	 * after page load.
	 * @param timeout Time to wait in milliseconds
	 */
	@Override
	public void waitForPageToLoad(String timeout) {
		super.waitForPageToLoad(timeout);
		//checkForServerConnection();
	}
	
	/**
	 * Another wrapper method, Get the time out properties from 
	 * the properties file.
	 */
	public void waitForPageToLoad() {
		this.waitForPageToLoad(SeleniumDefaultProperties.getResourceAsStream("default.pageload.timeout"));
	}
	
	/**
	 * wrapper method to DefualtSelenium class's method coz this method 
	 * accpet Integer instead of time out as a string value.
	 * @param timeout Time to wait in milliseconds
	 */
	public void waitForPageToLoad(int timeout) {
		waitForPageToLoad(Integer.toString(timeout));
	}
	
	/**
	 * Forces browser to break out into firebug in firefox.
	 */
	public void breakOut() {
		getEval("debugger;");
		pause(5000); //giving time to load.
	}
	
	/**
	 * Check for the server connection error Yukon specific error.
	 */
	public void checkForServerConnection() {
		try {
			if(super.isTextPresent("Connection to server has been lost") || 
					super.isVisible("//*[contains(text(), 'Connection to server has been lost')]")) {
				throw new SeleniumException("Cant Find Connection to Yukon Server");
			}
		}catch(SeleniumException ex){
			log.error(ex);
		}
	}
	
	/**
	 * wait for the given popup to load and open and switch the 
	 * focus to the popup window.
	 * @param windowName name of the window, can be found from firebug.
	 */
	public void waitForPopup(String windowName) {
		super.waitForPopUp(windowName, SeleniumDefaultProperties.getResourceAsStream("default.pageload.timeout"));
		super.selectWindow(windowName);
	}
	
    /**
     * Returns an array of absolute XPath expressions for all nodes that match
     * the given <code>xpath</code>.
     * <br/>
     *  
     * @param xpath - the XPath expression to evaluate.
     * @return A String array of absolute xpath.
     */
	public String[] explodeXpath(String xpath) {
		return commandProcessor.getStringArray("getExplodedXpath", new String[] {xpath,});
	}
	
	/**
	 * Wait for Ajax condition to complete.  
	 */
	public void waitForAjax() {
		super.waitForCondition("selenium.browserbot.getCurrentWindow().Ajax.activeRequestCount == 0;", 
		                        SeleniumDefaultProperties.getResourceAsStream("default.pageload.timeout"));
	}
	
	/**
	 * Method waits for the confirmation dialog, these are the dialogs that pops up during delete process.
	 * @param waitTime time to wait.
	 * @return return text from the confirmation dialog.
	 */
	public String waitForConfirmation(long waitTime) {
		while (waitTime > 0 && !isConfirmationPresent()) {
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				log.error("Thread was interrupted while in waitForConfirmation()", e);
			}
			waitTime -= 1000;
		}
		if(isConfirmationPresent()) {
			try {
				return getConfirmation();
			}catch (Exception e) {
				log.error("Unable to get confirmation window must have closed the confirmation due to ajax ", e );
			}
		}
		else
			throw new SeleniumException("ERROR: Time out while waiting for confirmation!");
		return null;
	}
	/**
	 * Wrapper method that uses wait time from seleniumdefault.properties file.
	 * @return
	 */
	public String waitForConfirmation() {
		return waitForConfirmation(Long.parseLong(SeleniumDefaultProperties.getResourceAsStream("default.pageload.timeout")));
	}

	/**
	 * TODO: Handle OS(Modal)to windows.
	 */
	public void handleModalWindow() {
		String js ="if(selenium.browserbot.getCurrentWindow().showModalDialog){";
		js +=  "if (typeof(selenium.browserbot.getCurrentWindow().g_selRetVar) == 'undefined') selenium.browserbot.getCurrentWindow().g_selRetVar = null;";
		js += "selenium.browserbot.getCurrentWindow()._UTL_SetSelRetVar = function (val){ window.g_selRetVar = val; window.status = window.g_selRetVar + ' is returned from child';};";
		js += "selenium.browserbot.getCurrentWindow().showModalDialog = function( sURL, vArguments, sFeatures)";
		js += "{if ((typeof(window.g_selRetVar) != 'undefined') && (window.g_selRetVar!=null)) {var temp = window.g_selRetVar; window.g_selRetVar = null; return temp;}";
		js += "selenium.browserbot.getCurrentWindow().open(sURL, 'modal', sFeatures);";
		js += "};}";
		super.getEval(js);
	}
	
	/**
	 * Helper method to get the url of the Yukon host machine.
	 * @return url of yukon application.
	 */
	public String getBaseURL() {
		return SeleniumDefaultProperties.getBaseHREF();
	}
	
	/**
	 * Overrides the attachFile method in {@link DefaultSelenium}.
	 * Attached a file based when given a input filed and location of the file.
	 * Location of the file needs to be a webserver Ex:http://some.com/file.csv.
	 * @param fieldLocator xpath to input field.
	 * @param fileLocator url to the webserver
	 */
	@Override
	public void attachFile(String fieldLocator, String fileLocator) {
		waitForElement(fieldLocator);
		if(!isElementPresent(fieldLocator))
			throw new SeleniumException("Unable to find input field.");
		super.attachFile(fieldLocator, fileLocator);
	}
	
	/**
	 * Override the answerOnNextPrompt method in {@link DefaultSelenium} to handle 
	 * windows popup windows.
	 * @param answer available option in popup windw (etc Save, Cancel).
	 */
	@Override
	public void answerOnNextPrompt(String answer) {
		pause(500);/*
		if(!super.isPromptPresent())
			throw new SeleniumException("Windows pop up window is didn't fireup.");*/
		super.answerOnNextPrompt(answer);
	}
	
	/**
	 * Check for HTTP500 errors, before wait for elements to load on any given page.
	 * 
	 */
	private void checkForHTTP500() {
		Locator errorReportInTitle = new CssLocator("title:contains('Error report')");
		Locator apacheInTitle = new CssLocator("title:contains('Apache Tomcat')");
		Locator description = new CssLocator("body:contains('The server encountered an internal error \\(\\) that prevented it from fulfilling this request.')");

		try {
			if(isElementPresent(apacheInTitle, Duration.ZERO) &&
			   isElementPresent(errorReportInTitle, Duration.ZERO) &&
			   isElementPresent(description, Duration.ZERO)) {
			    // TODO this path needs to be verified.
			    // OLD PATH >> "//p/b[text()='root cause']/following::pre";
			    Locator rootCause = new CssLocator("p:contains('root cause') pre");

			    String errorMsg = getText(rootCause);
				throw new SeleniumException(errorMsg);
			}
		} catch (SeleniumException ex) {
			log.error(ex);
		}
	}	
	
	public void select(Locator selectLocator, String optionLocator) {
	    select(selectLocator.generateLocatorString(), optionLocator);
	}
}
