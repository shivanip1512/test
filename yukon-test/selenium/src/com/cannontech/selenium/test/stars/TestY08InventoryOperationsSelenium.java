package com.cannontech.selenium.test.stars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.InventoryOperationsSolvent;

/**
 * This test verifies the functionality for the Inventory page
 * @author ricky.jones
 * @author anuradha.uduwage
 * @author kevin.krile
 */
public class TestY08InventoryOperationsSelenium extends SolventSeleniumTestCase {
	/**
	 * This method logs in as starsop/starsop
	 */
	public void init(){
		//Starts the session for the test
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");
	}

	/**
	 * This method selects inventory based upon some filter values with drop down menus
	 * This test depend on data in input file {@link TestY08InventoryOperationsSelenium.xml}
	 */
	@Test
	public void selectByFilter(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] rule = getParamStrings("InventoryByFilter");
		String[] ruleValue = getParamStrings("InventoryByFilter Value");
		String[] locator = getParamStrings("InventoryByFilter locator");
		String[] name = getParamStrings("InventoryByFilter Name");
		
		common.clickLinkByName("Inventory");
		for(int i=0;i<rule.length;i++){
			Assert.assertEquals("Operator: Inventory", common.getPageTitle());
			common.clickFormButton("inventory/setupFilterRules", "filterButton");
			common.selectDropDownMenu("inventory/applyFilter", "ruleType", rule[i]);
			common.clickFormButton("inventory/applyFilter", "addButton");
			common.selectDropDownMenu("inventory/applyFilter", locator[i],ruleValue[i]);
			common.clickFormButton("inventory/applyFilter", "apply");
			common.clickButtonBySpanText("Inventory Configuration");
			common.enterInputText("inventory/deviceReconfig/save", "name", name[i]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals(true, common.isLinkPresent(name[i]));
		}
		common.end();
	}

	/**
	 * This method selects inventory based upon some filter values with text boxes
	 * This test depend on data in input file {@link TestY08InventoryOperationsSelenium.xml}
	 */
	@Test
	public void selectByFilterText(){
		init();
		CommonSolvent common = new CommonSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		String[] rule = getParamStrings("InventoryByFilterText");
		String[] following = getParamStrings("Following 2");
		String[] ruleValue = {events.returnDate(-7), events.returnDate(0), common.getCurrentDate(), "40100000", "52000000", common.getCurrentDate()};
		
		common.clickLinkByName("Inventory");
		Assert.assertEquals("Operator: Inventory", common.getPageTitle());
		common.clickFormButton("inventory/setupFilterRules", "filterButton");
		for(int i=0;i<rule.length;i++){
			common.selectDropDownMenu("inventory/applyFilter", "ruleType", rule[i]);
			common.clickFormButton("inventory/applyFilter", "addButton");
		}
		for(int i=0;i<ruleValue.length;i++){
			common.enterInputText("inventory/applyFilter", following[i], ruleValue[i]);
		}
		common.clickFormButton("inventory/applyFilter", "apply");
		common.clickButtonBySpanText("Inventory Configuration");
		common.enterInputText("inventory/deviceReconfig/save", "name", "Multiple Filter Values");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals(true, common.isLinkPresent("Multiple Filter Values"));
		common.end();
	}

	/**
	 * This method selects inventory based upon some filter values with pop-up menus
	 * This test depend on data in input file {@link TestY08InventoryOperationsSelenium.xml}
	 * TODO may need to delete program - no data
	 */
	@Test
	public void selectByFilterPopup(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] rule = getParamStrings("InventoryByFilterPopup");
		String[] ruleValue = getParamStrings("InventoryByFilterPopup Value");
		String[] name = getParamStrings("InventoryByFilterPopup Name");
		
		for(int i=1;i<rule.length;i++){
			common.clickLinkByName("Inventory");
			Assert.assertEquals("Operator: Inventory", common.getPageTitle());
			common.clickFormButton("inventory/setupFilterRules", "filterButton");
			common.selectDropDownMenu("inventory/applyFilter", "ruleType", rule[i]);
			common.clickFormButton("inventory/applyFilter", "addButton");
			common.clickLinkBySpanText("None selected");
			common.clickLinkByNameWithoutPageLoadWait(ruleValue[i]);
			common.clickButtonByExactName("OK");
			common.clickFormButton("inventory/applyFilter", "apply");
			common.clickButtonBySpanText("Inventory Configuration");
			common.enterInputText("inventory/deviceReconfig/save", "name", name[i]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals(true, common.isLinkPresent(name[i]));
		}
		common.end();
	}

	/**
	 * This method selects inventory based upon some filter values with date ranges to be entered
	 * This test depend on data in input file {@link TestY08InventoryOperationsSelenium.xml}
	 */
	@Test
	public void selectByFilterCalendar(){
		init();
		CommonSolvent common = new CommonSolvent();
		EventsByTypeSolvent events = new EventsByTypeSolvent();
		String[] rule = getParamStrings("InventoryByFilterCalendar");
		String[] locator = getParamStrings("InventoryByFilterCalendar locator");
		String[] name = getParamStrings("InventoryByFilterCalendar Name");
		String[] date = {events.returnDate(-1), common.getCurrentDate(), common.getCurrentDate()};
		
		common.clickLinkByName("Inventory");
		for(int i=0;i<rule.length;i++){
			Assert.assertEquals("Operator: Inventory", common.getPageTitle());
			common.clickFormButton("inventory/setupFilterRules", "filterButton");
			common.selectDropDownMenu("inventory/applyFilter", "ruleType", rule[i]);
			common.clickFormButton("inventory/applyFilter", "addButton");
			common.enterInputText("inventory/applyFilter", locator[i],date[i]);
			if(rule[i].equals("filterRules[0].deviceStateDateFrom"))
				common.enterInputText("inventory/applyFilter", "filterRules[0].deviceStateDateTo", events.returnDate(0));
			common.clickFormButton("inventory/applyFilter", "apply");
			common.clickButtonBySpanText("Inventory Configuration");
			common.enterInputText("inventory/deviceReconfig/save", "name", name[i]);
			common.clickButtonBySpanText("Save");
			Assert.assertEquals(true, common.isLinkPresent(name[i]));
		}
		common.end();
	}
	
	/**
	 * This test case will verify that an Inventory Configuration Task can be created by selecting inventory manually.
	 */
	@Test
	public void selectByInventory() {
		init();
		CommonSolvent common = new CommonSolvent();
		String[] selectableInventory = getParamStrings("InventoryManual");
		
		common.clickLinkByName("Inventory");
		PopupMenuSolvent inventoryPicker = PickerFactory.createPopupMenuSolvent("inventory", PickerType.SingleSelect);
		common.clickButtonBySpanTextWithElementWait("Select by Inventory");
		for(int i = 0; i < selectableInventory.length; i++)
			inventoryPicker.clickMenuItem(selectableInventory[i]);
		common.clickButtonByNameWithPageLoadWait("OK");
		//popup solvent doesn't work right unless popup menu has picker in the ID
//		new WidgetSolvent().clickLinkByWidget("Select An Action To Perform On Selected Inventory", "Individually Selected Inventory");
//		for(int i = 0; i < selectableInventory.length; i++)
//			Assert.assertEquals(selectableInventory[i]+" was added to the selected inventory.",true, common.isTextPresent(selectableInventory[i]));
//		new MultiSelectPopupMenuSolvent("menuId=inventoryCollection").closePopupMenu();
		Assert.assertEquals("The inventory count is "+selectableInventory.length, true, common.isTextPresent(Integer.toString(selectableInventory.length)));
		common.clickButtonBySpanText("Inventory Configuration");
		common.enterInputText("inventory/deviceReconfig/save", "name", "InventoryTest");
		common.clickButtonBySpanText("Save");	
		common.clickButtonByTitle("Delete: InventoryTest");
		Assert.assertEquals("The saved inventory count is "+selectableInventory.length, true, common.isTextPresent(Integer.toString(selectableInventory.length)));
		Assert.assertEquals("InventoryTest was saved successfully.", true, common.isTextPresent("InventoryTest"));
		Assert.assertFalse(common.isLinkPresent("InventoryTest"));
		common.end();
	}	
	
	/**
	 * This test case will verify that an Inventory Configuration Task can be created by selecting inventory by file uploads.
	 */
	@Test
	public void selectByFileUpload() {
		init();
		CommonSolvent common = new CommonSolvent();
		String[] uploadedDevices = getParamStrings("InventoryByFile");
		
		common.clickLinkByName("Inventory");
		common.clickButtonBySpanTextWithElementWait("Select by File Upload");
		common.enterInputText("inventory/uploadFile", "fileUpload.dataFile", getParamString("InventoryData"));
		common.clickButtonBySpanText("OK");
		//popup solvent doesn't work right unless popup menu has picker in the ID
//		new WidgetSolvent().clickLinkByWidget("Select An Action To Perform On Selected Inventory", "Inventory Selection.csv");
//		for(int i = 0; i < uploadedDevices.length; i++)
//			Assert.assertEquals(uploadedDevices[i]+" was added to the selected inventory.",true, common.isTextPresent(uploadedDevices[i]));
//		new MultiSelectPopupMenuSolvent("menuId=inventoryCollection").closePopupMenu();
		Assert.assertEquals("The uploaded count is "+uploadedDevices.length, true, common.isTextPresent("Inventory Count: " + Integer.toString(uploadedDevices.length)));
		common.clickButtonBySpanText("Inventory Configuration");
		common.enterInputText("inventory/deviceReconfig/save", "name", "UploadTest");
		common.clickButtonBySpanText("Save");
		common.clickButtonByTitle("Delete: UploadTest");
		Assert.assertEquals("The saved uploaded count is "+uploadedDevices.length, true, common.isTextPresent(Integer.toString(uploadedDevices.length)));
		Assert.assertEquals("UploadTest was saved successfully.", true, common.isTextPresent("UploadTest"));
		Assert.assertFalse(common.isLinkPresent("UploadTest"));
		common.end();
	}
	
	/**
	 * This test verifies command scheduling options and configurability
	 */
	@Test
	public void commandScheduling() {
		init();
		InventoryOperationsSolvent inventory = new InventoryOperationsSolvent();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("Inventory");
		common.clickButtonBySpanText("Create");
		common.enterInputText("inventory/updateSchedule", "runPeriodHours", "14");
		common.clickButtonBySpanText("Save");
		Assert.assertEquals("Daily schedule has been created.", true, common.isTextPresent("Daily, at 01:00 AM"));
		inventory.disableCommandSchedule("Daily, at 01:00 AM");
		inventory.enableCommandSchedule("Daily, at 01:00 AM");
		common.clickLinkBySpanText("Daily, at 01:00 AM");
		common.clickButtonBySpanTextWithElementWait("Delete");
		common.clickFormButton("inventory/updateSchedule", "delete");
		Assert.assertEquals("Daily schedule has been deleted.", true, common.isTextPresent("Command schedule entry deleted successfully."));
		common.end();
	}

}