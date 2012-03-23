package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * @author AF835
 */
public class TestDeviceGroupsUploadSelenium extends SolventSeleniumTestCase {
	/**
	 * This test logs into yukon and uploads a device group with meters.
	 */
	@Test
	public void uploadDeviceGroups() {
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		// Select Device Groups Upload
		topMenu.clickTopMenuItem("Device Groups");
		topMenu.clickTopSubMenuItem("Upload");
		Assert.assertEquals("Device Group Updater",common.getPageTitle());
		metering.selectOptionsCheckbox();
		
		// Select File to Upload
		common.enterInputText("updater/parseUpload", "dataFile",getParamString("UploadFile"));
		
		// Upload
		common.clickFormButtonByButtonValue("updater/parseUpload", "Process");
		Assert.assertTrue(common.isTextPresent("Successfully updated 1 meter's device groups"));
		common.end();
	}
}