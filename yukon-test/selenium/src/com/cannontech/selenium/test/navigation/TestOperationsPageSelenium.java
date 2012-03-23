package com.cannontech.selenium.test.navigation;

import org.junit.Test;
import org.testng.Assert;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
/**
 * @author anuradha.uduwage
 */
public class TestOperationsPageSelenium extends SolventSeleniumTestCase {
	@Test
	public void navigateFromOperationsPage() {
		start();
		CommonSolvent common = new CommonSolvent();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		login.cannonLogin("syukon", "syukon");
		common.clickLinkByName("Metering");
		Assert.assertEquals(common.getPageTitle(), "Metering");
		login.end();
	}
	@Test
	public void anotherNavigate() {
		start();
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CommonSolvent common = new CommonSolvent();
		login.cannonLogin("syukon", "syukon");
		common.clickLinkByName("All Trends");
		Assert.assertEquals(common.getPageTitle(), "Metering");
		topMenu.clickAllTrendsHome();
		common.clickLinkByName("Bulk Operations");
		topMenu.clickHome();
		common.clickLinkByName("Metering");
		common.end();
	}
}