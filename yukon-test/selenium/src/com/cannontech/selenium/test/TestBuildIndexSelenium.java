/**
 * 
 */
package com.cannontech.selenium.test;

import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.test.common.CommonHelper;

/**
 * This class runs as a test but main purpose is to cleanup the indexes prior to running all the ohter test. 
 * @author anuradha.uduwage
 *
 */
public class TestBuildIndexSelenium extends SolventSeleniumTestCase {
	
	/**
	 * Login to open a session.
	 */
	public void init() {
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin("yukon", "yukon");
	}	

	/**
	 * Method click all for links to build the indexes.
	 */
	@Test
	public void buildIndex() {
		init();
		new OperationsPageSolvent().clickLinkItem("Manage Indexes");
		CommonHelper helper = new CommonHelper();
		helper.buildAllIndexes();
	}
}
