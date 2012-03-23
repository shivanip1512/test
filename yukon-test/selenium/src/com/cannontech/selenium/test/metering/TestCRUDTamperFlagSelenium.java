/**
 * 
 */
package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.OperationsPageSolvent;
import com.cannontech.selenium.solvents.common.WidgetSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;

/**
 * This Test class contains method to test Tamper Flag process and its functions.
 * @author anuradha.uduwage
 *
 */
public class TestCRUDTamperFlagSelenium extends SolventSeleniumTestCase {
	
	String[] tamperFlags = null;
	String[] editTamperFlags = null;
	/**
	 * Login and add tampler flag names to an array.
	 */
/*	public void init() {
		start();
		//login
		LoginLogoutSolvent login = new LoginLogoutSolvent();
		login.cannonLogin("syukon", "syukon");
		//reading in data from xml
		tamperFlags = getParamStrings("tamperFlagName");	
		editTamperFlags = getParamStrings("editTamperFlagName");
	} */
	
	/**
	 * Test case navigate to Metering page and create a Tamper Flag.<br>
	 * During this process method selects the Device Group for the new Tamper Flag.
	 */
	@Test
	public void createTamperFlag() {
/*		init();
		//getting into metering page
		new OperationsPageSolvent().clickLinkItem("Metering");
		MeteringSolvent meteringSolvent = new MeteringSolvent();
		CommonSolvent commonSolvent = new CommonSolvent();
		
		for (String tamperFlag : tamperFlags) {
			meteringSolvent.clickCreateByWidget("Tamper Flag Monitors");
			commonSolvent.enterText("Name:", tamperFlag);
			meteringSolvent.clickDeviceGroupIcon();
			
			//selecting a device group for tamper flag.
			DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
			dGroup.clickCloseIcon();
			dGroup.clickDeviceGroupIcon();
			dGroup.expandByName("System").expandChildByParent("System", "Meters");
			dGroup.expandChildByParent("Meters", "Scanning").selectTreeNode("Load Profile");
			dGroup.expandByName("Meters").expandByName("Flags").selectTreeNode("UsageMonitoring").clickSelectGroupButton();
			commonSolvent.clickButtonByName("Save");
			
			WidgetSolvent widget = new WidgetSolvent();
			//some validation on pages
			Assert.assertEquals("Metering", commonSolvent.getPageTitle());
			widget.clickLinkByWidget("Tamper Flag Monitors", tamperFlag);
			Assert.assertEquals("Tamper Flag Processing", commonSolvent.getPageTitle());
			new YukonTopMenuSolvent().clickTopMenuItem("Metering");
		}*/
	} 
	
	/**
	 * By default all the tamper flags are unable, this method will disable each tamper flag and 
	 * confirm that its disable.
	 */
/*	@Test
	public void disableTamperFlag() {
		init();
		new OperationsPageSolvent().clickLinkItem("Metering");
		CommonSolvent commonSolvent = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		for (String tamperFlag : tamperFlags) {
			widget.disableByWidgetAndMonitorName("Tamper Flag Monitors", tamperFlag);
			widget.clickLinkByWidget("Tamper Flag Monitors", tamperFlag);
			commonSolvent.clickButtonByName("Edit");
			Assert.assertEquals("Tamper Flag Monitoring", commonSolvent.getPageTitle());
			Assert.assertEquals(true, commonSolvent.isTextPresent("Disable"));
			new YukonTopMenuSolvent().clickTopMenuItem("Metering");
		}
	} */
	
	/**
	 * Edit tamper flags.
	 */
/*	@Test
	public void editTamperFlag() {
		init();
		new OperationsPageSolvent().clickLinkItem("Metering");
		CommonSolvent commonSolvent = new CommonSolvent();
		WidgetSolvent widget = new WidgetSolvent();
		for (int i = 0; i < tamperFlags.length; i++) {
			widget.clickLinkByWidget("Tamper Flag Monitors", tamperFlags[i]);
			commonSolvent.clickButtonByName("Edit");
			commonSolvent.enterText("Name:", editTamperFlags[i]);
			commonSolvent.clickButtonByName("Update");
		}
		//doing assertion to see if edit process work.
		for(String editedTamperFlag : editTamperFlags) {
			Assert.assertEquals(true, commonSolvent.isLinkPresent(editedTamperFlag));
		}
	} */
	
	/**
	 * Test to delete all the tamper flags created and edited in {@link #createTamperFlag(), #editTamperFlag()}. 
	 */

/*	@Test
	public void deleteTamperFlag() {
		init();
		new OperationsPageSolvent().clickLinkItem("Metering");
		CommonSolvent commonSolvent = new CommonSolvent();
		
		for (String editedTamperFlag : editTamperFlags) {
			WidgetSolvent widget = new WidgetSolvent();
			//some validation on pages
			Assert.assertEquals("Metering", commonSolvent.getPageTitle());
			widget.clickLinkByWidget("Tamper Flag Monitors", editedTamperFlag);
			Assert.assertEquals("Tamper Flag Processing", commonSolvent.getPageTitle());
			commonSolvent.clickButtonByName("Edit");
			Assert.assertEquals("Tamper Flag Monitoring", commonSolvent.getPageTitle());
			commonSolvent.clickButtonByName("Delete").clickOkOnConfirmation();
		}
	} */
}
