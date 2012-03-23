package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;

/**
 * This class contain test cases to edit, delete move meter groups and sub groups.
 * @author anuradha.uduwage
 *
 */
public class TestCRUDMeterGroupSelenium extends SolventSeleniumTestCase {
	/**
	 * Test cases navigates to Device Group page and create a create, edit 
	 * and move a new sub group.
	 */
	@Test
	public void addEditMoveMeterGroups() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		DeviceGroupPopupSolvent dGroup = new DeviceGroupPopupSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		new YukonTopMenuSolvent().clickTopMenuItem("Device Groups");
		
		dGroup.expandByName("System").expandChildByParent("System", "Meters");
		dGroup.expandChildByParent("Meters", "Scanning").selectTreeNode("Load Profile");
		dGroup.collapseByName("Scanning");
		dGroup.expandByName("Meters").expandByName("Flags").selectTreeNode("UsageMonitoring");
		
		common.clickLinkByNameWithoutPageLoadWait("Add Subgroup");
		common.enterInputText("editor/addChild", "childGroupName", "Selenium");
		common.selectDropDownMenu("editor/addChild", "subGroupType", "Composed");
		common.clickButtonByNameWithPageLoadWait("Create");
		
		Assert.assertEquals("Composed Device Group Setup", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("Composed Device Group Setup"));
		common.clickButtonByName("Save");
		
		dGroup.collapseByName("UsageMonitoring").collapseByName("Flags").collapseByName("Meters");
		dGroup.expandByName("Meters").expandByName("Flags").expandByName("UsageMonitoring").selectTreeNode("Selenium");
		common.clickLinkByName("Delete Group");
		common.clickOkOnConfirmation();
		common.end();
	}
}
