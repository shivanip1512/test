/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.SeleniumSession;
import com.cannontech.selenium.core.SolventSelenium;

/**
 * TODO: Idea of this class is to provide highlevel navigation functionalities.
 * @author anuradha.uduwage
 *
 */
public class NavigationHelper {

	public static void waitForPageContent() {
		int timeout = 30000;
		while(timeout > 0 && !Boolean.parseBoolean(getSelenium().getEval(
				"")));
	}	
	
	protected static SolventSelenium getSelenium() {
		return SeleniumSession.get();
	}
}
