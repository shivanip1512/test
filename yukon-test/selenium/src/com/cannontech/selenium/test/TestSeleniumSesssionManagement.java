/**
 * 
 */
package com.cannontech.selenium.test;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SeleniumDefaultProperties;
import com.cannontech.selenium.core.SeleniumSession;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Test Session, ThreadLocal implementation.
 * @author anuradha.uduwage
 *
 */

public class TestSeleniumSesssionManagement extends SolventSeleniumTestCase {
	
	@Test
	public void testSessionCaching() {

		start(getAuthenticatedSeleniumSession(), new LoginLogoutSolvent()).cannonLogin("yukon", "yukon");
		java.net.InetAddress localMachine;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			System.out.println(localMachine.getHostName());
			System.out.println(localMachine.getHostAddress());
			System.out.println(SeleniumDefaultProperties.getClassInstance().getResourceAsStream("default.auth.url"));
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(System.getProperty("server.url"));
		// Store this session
		SeleniumSession.store("Session1");

		System.out.println(SeleniumSession.get().toString());
		// Start a new session
		start(getAuthenticatedSeleniumSession());

		// Restore the old session
		SeleniumSession.restore("Session1");

		// Attempt to restore a non-existant session -- should throw an
		// exception
		boolean exceptionCaught = false;

		try {
			SeleniumSession.restore("NonExistant");
		} catch (SeleniumException e) {
			exceptionCaught = true;
		}
		
		Assert.assertTrue(
				"Attempting to restore a non-existant session did not throw an exception",
				exceptionCaught);

		SeleniumSession.endSession();
	}
}
