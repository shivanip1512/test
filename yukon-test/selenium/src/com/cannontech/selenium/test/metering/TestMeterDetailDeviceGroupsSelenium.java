package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * The following tests test the functionality and verification of adding a remove
 * a meter from a Device Group through the Device Group widget on the Meter Details
 * page. QA-105. 
 * @author Jon Narr
 */
public class TestMeterDetailDeviceGroupsSelenium extends SolventSeleniumTestCase {
	public void init() {
		//Starts the session for the test. 
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	
	/**
	 * This test logs into yukon and attempts add a meter to a Device Group.
	 */

	@Test
	public void addMeterToDeviceGroup() {
		init();
		//start the session for the test. 
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "a_MCT-410CL");
        common.clickButtonBySpanText("Search");

		//Verify meter was found
		Assert.assertTrue(common.isTextPresent("a_MCT-410CL"));
		//Verify meter's status is enabled
		Assert.assertTrue(common.isTextPresent("Enabled"));
		
		// Add meter to Device Group Meters
		common.clickButtonBySpanTextWithElementWait("Add to Groups");
		dGroup.expandByName("Groups");
		dGroup.expandByName("Meters");
		dGroup.expandByName("Flags");
		dGroup.selectTreeNode("UsageMonitoring");
		dGroup.clickAddDeviceToSelectedGroupButton();
		
		// Verify meter is added to group
		Assert.assertTrue(common.isLinkPresent("/Meters/Flags/UsageMonitoring"));
		common.end();
	}

	/**
	 * This test logs into yukon and attempts removing a meter to a Device Group.
	 */
	@Test
	public void removeMeterFromDeviceGroup() {
		init();
		//start the session for the test. 
		CommonSolvent common = new CommonSolvent();
		MeteringSolvent metering = new MeteringSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
        common.enterInputTextByFormId("filterForm", "Quick Search", "a_MCT-410CL");
		common.clickButtonBySpanText("Search");
		
		//Verify meter was found
		Assert.assertTrue(common.isTextPresent("a_MCT-410CL"));
		//Verify meter's status is enabled
		Assert.assertTrue(common.isTextPresent("Enabled"));
		
		// Verify device group currently exists
		Assert.assertTrue(common.isTextPresent("/Meters/Flags/UsageMonitoring"));
		// Remove device group
		metering.removeGroupFromDeviceGroup("Device Groups", "/Meters/Flags/UsageMonitoring");
		common.end();
	}
}
