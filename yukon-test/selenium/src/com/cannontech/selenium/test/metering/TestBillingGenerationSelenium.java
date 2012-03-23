package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.common.YukonTreeSolvent;

/**
 * This class tests searches for a Meter,then verifies the ability to disconnect and connect MCT 410.
 * @author anjana.manandhar
 */
public class TestBillingGenerationSelenium extends SolventSeleniumTestCase{
	public void init() {
		//Starts the new session
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
	}
	/**
	 * Method verifies the ability to Generate the Billing Files.
	 */
	@Test
	public void verifyBillingGeneration() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		YukonTreeSolvent treeSolvent = new YukonTreeSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering",common.getPageTitle());
		topMenu.clickTopMenuItem("Billing");
		topMenu.clickTopSubMenuItem("Billing Generation");
		Assert.assertEquals("Billing",common.getPageTitle());
		common.selectDropDownMenu("servlet/BillingServlet", "fileFormat", "CTI-CSV");
		treeSolvent.expandByName("Meters");
		treeSolvent.selectTreeNode("Meters");
		common.clickButtonByName("Generate");
		// TODO We should add something here to verify that there is a file dialog
		common.end();
	}
}