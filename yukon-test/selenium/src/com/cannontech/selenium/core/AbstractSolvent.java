/**
 * 
 */
package com.cannontech.selenium.core;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

/**
 * Abstract class that should get extended by any Solvent class. Default constructor accepts 
 * multiple parameters and setup instance of selenium session.
 * @author anuradha.uduwage
 *
 */
public abstract class AbstractSolvent {
	
	private static final Logger log = Logger.getLogger(AbstractSolvent.class.getName());
	
	private final HashMap<String, String> values = new HashMap<String, String>();
	
	/**
	 * we introduce this instance for convenience so we dont have to use getSeleniumDriver() all the time.
	 */
	protected SolventSelenium selenium = null;
	
	protected AbstractSolvent() {
		//so the selenium instance will be in sync with ThreadLocal session at all times.
		selenium = getSeleniumDriver();
	}
	
	public AbstractSolvent(String...params) {
		for (String param:params) {
			try {
				String keyName = param.substring(0, param.indexOf('=')).trim();
				String keyValue = param.substring(param.indexOf("=")+1).trim();
				if(!values.containsKey(keyName))
					values.put(keyName, keyValue);
				else
					log.warn("Same value twice " + keyName + " Ignoring");
			} catch (Exception e) {
				log.error("Error evaulating Solvent Parameter '" + param + 
						"'.  Correct format is 'name=value'. This parameter will be ignored.\n" + e.getMessage());
			}
		}
		selenium = SeleniumSession.get();
	}
	
	/**
	 * Getter method for Thread Local Session
	 * @return selenium session.
	 */
	public SolventSelenium getSeleniumDriver() {
		return SeleniumSession.get();
	}
	
	/**
	 * Setter method for ThreadLocal
	 * @param selenium
	 */
	public void setSeleniumDriver(SolventSelenium selenium) {
		SeleniumSession.set(selenium);
		this.selenium = getSeleniumDriver();
	}
	
	public String getParams(String name) {
		if(!values.containsKey(name))
			log.error("Requested Value " + name + " doesn't exist, return null");
		return values.get(name);
	}
	
	public void setParams(String name, String value) {
		values.put(name, value);
	}
	
	protected AbstractSolvent(SolventSelenium solventSelenium) {
		this.setSeleniumDriver(solventSelenium);
	}

	/**
	 * Abstract method that should implement necessary code that required to prepare the application
	 * to be use by the solvent. Method is called by navigateTo()
	 */
	public abstract void prepare();
	
	/**
	 * Generic method that accept a solvent and invoke its prepare method
	 * and return the active solvent.
	 * @param <X>
	 * @param anySolvent
	 * @return
	 */
	public <X extends AbstractSolvent>X navigateTo(X anySolvent) {
		anySolvent.prepare();
		return anySolvent;
	}
	
	/**
	 * Calls the session stop() method that sends "testComplete" command to the selenium
	 * server to shutdown the browser session.
	 */
	public void end() {
		getSeleniumDriver().stop();
		setSeleniumDriver(null);
	}
	
	
	
	// Assert Delay Helper Methods
    /**
     * This method takes in a wait time in seconds and 
     */
    protected Duration delayForRecheck(Duration wait, Duration delay){
        selenium.pause(delay.getMillis());
        return wait.minus(delay);
    }
    
    /**
     * This method is a helper method for delayForRecheck(int).  It supplies a system default value for the wait time. 
     * @return
     */
    protected Duration delayForRecheck(Duration wait) {
        return delayForRecheck(wait, Duration.millis(100));
    }

}
