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
public class TestDeviceGroupsAttributeReadSelenium extends SolventSeleniumTestCase {
	/**
	 * This test logs into yukon and sends an attribute to a group of meters in a device group.
	 */
	@Test
	public void sendGroupAttributeRead() {
		// Login
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");	
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		// Select Device Groups Group Attribute Read
		topMenu.clickTopMenuItem("Device Groups");
		topMenu.clickTopSubMenuItem("Group Attribute Read");
		Assert.assertEquals("Group Attribute Read",common.getPageTitle());
		
		// Select Attribute
		common.selectFromMultiListBox("Select Attribute(s):", "Blink Count");
		
		// Select Device Group
		dGroup.expandByName("Meters").expandByName("Collection").selectTreeNode("410 Manual Freeze Group");
		
		// Read Attribute
		common.clickFormButtonByButtonValue("groupMeterRead/readGroup", "Read");
		
		// Verify Result
		Assert.assertEquals("Group Attribute Read Result Detail",common.getPageTitle());
		Assert.assertTrue(common.isTextPresent("Complete"));
		common.end();
	}
}