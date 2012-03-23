/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Duration;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.input.CSVDataFileDigester;
import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;
import com.cannontech.selenium.solvents.common.userLogin.model.Login;

/**
 * This class handles Yukon login and logout functionalities 
 * @author anuradha.uduwage
 *
 */
public class LoginLogoutSolvent extends AbstractSolvent {

    private static final String loginFormAction = "LoginController";
    public static final Login DEFAULT_CTI_LOGIN = new Login("DefaultCTI", "$cti_default");
    
	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}

	public void cannonLogin(Login login) {
	    cannonLogin(login.getUsername(), login.getPassword1());
	}
	
	/**
	 * API level method that accept any username and password as a string. 
	 * @param userName String value of the username.
	 * @param password String value of the password.
	 * @return returns an instance of {@link LoginLogoutSolvent}
	 */
	public LoginLogoutSolvent cannonLogin(String userName, String password) {
	    CommonSolvent common = new CommonSolvent();

	    // This is used for IE to make sure the session didn't carry over to the new IE window.
	    attemptLogout();

	    common.enterInputText(loginFormAction, "USERNAME", userName);
	    common.enterInputText(loginFormAction, "PASSWORD", password);
		
	    common.clickFormButton(loginFormAction, "login");

	    return this;	
	}

	/**
	 * Multiple user login using user credentials from CSV file.
	 * @param filename name of the file that has Usernames and Passwords.
	 * @return Returns an instance of {@link LoginLogoutSolvent}
	 * @throws FileNotFoundException Throws an exception if file not found.
	 * @throws InterruptedException 
	 */
	public LoginLogoutSolvent multipleUserLogin(String filename) throws FileNotFoundException, InterruptedException {
		CSVDataFileDigester readFromCSV = new CSVDataFileDigester();
		Collection<String> collection = null;
		collection = readFromCSV.parse(filename);
		HashMap<String, String> params = null;
		params = readFromCSV.returnStringParams(collection);
		Iterator<Entry<String, String>> i = params.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry<String, String> values = (Map.Entry<String, String>)i.next();
			cannonLogin(values.getKey(), values.getValue());
			yukonLogout();
		}
		return this;
	}
	
	/**
	 * Logout from Yukon webapplication.
	 * @return return instance of {@link LoginLogoutSolvent}
	 */
	public LoginLogoutSolvent yukonLogout() {
		Locator logoutPath = new CssLocator("a:contains('Logout')");

		selenium.waitForElement(logoutPath, Duration.standardSeconds(5));
		selenium.click(logoutPath);
		selenium.waitForPageToLoad();
		return this;
	}	
	
	/**
     * Logout from Yukon webapplication if it isn't already logged out.  
     * This is meant to be used for IE since it doesn't take care of it's session.
     */
    private void attemptLogout() {
        Locator logoutPath = new CssLocator("a:contains('Logout')");

        if (selenium.isElementPresent(logoutPath, Duration.ZERO)) {
            selenium.click(logoutPath);
            selenium.waitForPageToLoad();
        }
    }
}
