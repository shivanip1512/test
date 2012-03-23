/**
 * 
 */
package com.cannontech.selenium.core;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.thoughtworks.selenium.SeleniumException;

/**
 * This class will handle the storage of the selenium session in a ThreadLocal. It provides 
 * setters and getters and cache, with store() and restore() methods. In this way we dont need 
 * pass the selenium session back and forth between Solvents. instead each solvent can simply 
 * access the selenium session.
 * 
 * @author anuradha.uduwage
 *
 */
public class SeleniumSession {
	
	private static final Logger log = Logger.getLogger(SeleniumSession.class.getName());

	private static ThreadLocal<HashMap<String, SolventSelenium>> cache = 
		new ThreadLocal<HashMap<String,SolventSelenium>>() {
		protected synchronized HashMap<String, SolventSelenium> initialvalue() {
			return new HashMap<String, SolventSelenium>();
		}
	};
	
	private static ThreadLocal<SolventSelenium> selenium = new ThreadLocal<SolventSelenium>();
	
	/**
	 * Get the current selenium session.
	 * @return
	 */
	public static SolventSelenium get() {
		return selenium.get();
	}
	
	/**
	 * Set the current selenium session.
	 * @param classInstance
	 */
	public static void set(SolventSelenium classInstance) {
		selenium.set(classInstance);
	}
	
	/**
	 * Method to end the session, should use after the test.
	 */
	public static void endSession() {
		selenium.get().stop();
	}
	
	/**
	 * This method attaches a name to the current session and stored it in the session cache of the current thread.
	 * @param name The name to apply to the session that can be use later to retrieve the session 
	 * from cache via the restore() method.
	 */
	public static void store(String name) {
		cache.get().put(name, selenium.get());
	}
	
	public static void restore(String sessionName) {
		if(!cache.get().containsKey(sessionName)) {
			throw new SeleniumException("Cannot restore session named " + sessionName + ". Check if you saved the " +
					"session at the first place HINT: " + " Use SeleniumSession.store(<NAME>) to store a session " +
					"to be retrieved later by using restore().");
		}
		else {
			log.trace("Restoring session " + sessionName);
			selenium.set(cache.get().get(sessionName));
		}
	}
	
}
