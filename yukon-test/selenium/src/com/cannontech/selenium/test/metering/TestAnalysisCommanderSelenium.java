package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;

/**
 * @author AF835
 *
 */
public class TestAnalysisCommanderSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test.
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	/**
	 * This test logs into yukon and attempts to read a meter's usage, previous usage,
	 * total consumption and demand and verify that a second reading is greater than 
	 * or equal to the original values.
	 */
	@Test
	public void executeCommonCommands() {
		init();
		//start the session for the test. 
		CommonSolvent common = new CommonSolvent();

		new OperationsPageSolvent().clickLinkItemByCategory("Analysis", "Commander");
		Assert.assertEquals("Energy Services Operations Center", common.getPageTitle());
		common.selectDropDownMenuByAttributeName("SearchBy", "Device Name");
		common.enterTextByAttribute("name", "SearchValue", "MCT-310iL-01 (228711)");
		common.clickButtonByNameWithPageLoadWait("Search");
		common.clickLinkByName("MCT-310iL-01 (228711)");
		common.selectDropDownMenu("servlet/CommanderServlet", "commonCommand", "Read Energy");
		common.clickButtonByNameWithElementWait("Execute", "//div[@class='scroll']/span", 15000);
		common.isTextPresent("Emetcon DLC command sent on route");
		common.end();
	}	
}
