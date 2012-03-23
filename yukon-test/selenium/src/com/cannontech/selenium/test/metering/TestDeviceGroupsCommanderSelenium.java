package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;

/**
 * @author AF835
 */
public class TestDeviceGroupsCommanderSelenium extends SolventSeleniumTestCase {
	/**
	 * This test logs into yukon and sends commands to a device group of meters.
	 */
	@Test
	public void sendGroupCommand() {
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		common.clickLinkByName("Metering");
		
		// Select Device Groups Commander
		topMenu.clickTopMenuItem("Device Groups");
		topMenu.clickTopSubMenuItem("Commander");
		Assert.assertEquals("Group Processing",common.getPageTitle());
		
		// Select Command to Execute
		common.selectDropDownMenu("commander/executeGroupCommand", "commandSelectValue", "Read Energy");
		
		// Select Device Group
		dGroup.expandByName("Meters").expandByName("Collection").selectTreeNode("410 Manual Freeze Group");
		
		// Execute Command
		common.clickFormButtonByButtonValue("commander/executeGroupCommand", "Execute");
		
		// Verify Result
		Assert.assertEquals("Group Command Processing Result Detail",common.getPageTitle());
		common.isTextPresent("Complete");
		common.end();
	}
}