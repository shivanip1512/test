package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeterItemPickerSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;
/**
 * This test class handle Create, delete, and update actions on Outage Monitoring. 
 * @author anuradha.uduwage
 */
public class TestCRUDOutageMonitorSelenium extends SolventSeleniumTestCase {
	/**
	 * Start get used in all the methods, so extract to a common method.
	 */
	public void init() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");		
	}
	/**
	 * Test add a device to the device groups.
	 */
	@Test
	public void addDeviceToGroup() {
		init();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		MeterItemPickerSolvent iPicker = new MeterItemPickerSolvent();
		common.clickLinkByName("Metering");
		topMenu.clickTopMenuItem("Device Groups");
		
		dGroup.expandByName("Meters").selectTreeNode("Alternate");
		common.clickLinkByName("Add Devices").clickButtonByName("Select By Device");
		//searching device.
		iPicker.enterQueryTerm("MCT");
		iPicker.clickLinkInARow("a_MCT-410IL");
		common.clickButtonByNameWithPageLoadWait("OK");
		
		//confirm that device got added.
		Assert.assertEquals("Groups Home", common.getPageTitle());
		Assert.assertEquals("a_MCT-410IL", common.getLinkText("a_MCT-410IL"));
		common.end();
	}
	/**
	 * Create Outage monitor for the device.
	 */
	@Test
	public void createOutageMonitor() {
		init();
		MeteringSolvent meteringSolvent = new MeteringSolvent();
		CommonSolvent common= new CommonSolvent();
		DeviceGroupPopupSolvent deviceGroup = new DeviceGroupPopupSolvent();
		common.clickLinkByName("Metering");
		
		//click create outage monitor button in widget
		meteringSolvent.clickCreateByWidget("Outage Monitors");
		common.enterInputText("monitorEditor/update", "name", "demoOutage");
		
		//opening device group popup window
		deviceGroup.clickDeviceGroupIcon();
		//expand the tree
		deviceGroup.expandByName("Meters");
		deviceGroup.selectTreeNode("Alternate").clickSelectGroupButton();
		//edit outage monitor settings
		common.enterInputText("monitorEditor/update", "numberOfOutages", "5");
		common.clickButtonByName("Save");
		common.end();
	}
	/**
	 * Delete outage monitor.
	 */
	@Test
	public void deleteOutageMonitor() {
		init();
		CommonSolvent common= new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		common.clickLinkByName("Metering");
		//some validation on pages
		Assert.assertEquals("Metering", common.getPageTitle());

		widget.clickLinkByWidgetWithPageLoadWait("Outage Monitors", "demoOutage");
		Assert.assertEquals("Outage Processing", common.getPageTitle());
		common.clickButtonByNameWithPageLoadWait("Edit");

		Assert.assertEquals("Metering: Edit Monitor (demoOutage)", common.getPageTitle());
		common.clickButtonByName("Delete").clickOkOnConfirmation();
		common.end();
	}
	
	/**
	 * Remove the device from the device group.
	 */
	@Test
	public void removeDeviceFromGroup() {
		init();
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		
		common.clickLinkByName("Metering");
		new YukonTopMenuSolvent().clickTopMenuItem("Device Groups");
		Assert.assertEquals("Groups Home", common.getPageTitle());
		dGroup.expandByName("Meters");
		dGroup.selectTreeNode("Alternate");
		new MeteringSolvent().clickDeleteImgByDeviceName("a_MCT-410IL");
		common.end();
	}

}
