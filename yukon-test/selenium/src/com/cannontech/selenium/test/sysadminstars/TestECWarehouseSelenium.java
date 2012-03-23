package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile	
 */
public class TestECWarehouseSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		new LoginLogoutSolvent().cannonLogin("starsop11", "starsop11");
	}
	private String action = "warehouse/update";
	/**
	 * This test logs in as starsop11 and creates a Warehouse for QA_Test10
	 * This test depend on data in input file {@link TestECWarehouseSelenium.xml}
	 */
	@Test
	public void addWarehouse(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name");
		String[] notes = getParamStrings("Notes");
		String[] addr1 = getParamStrings("Street Address 1");
		String[] addr2 = getParamStrings("Street Address 2");
		String[] city = getParamStrings("City");
		String[] state = getParamStrings("State");
		String[] zip = getParamStrings("Zip");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Warehouses");	
		for(int i=0;i<2;i++){
			common.clickButtonBySpanText("Create");
			common.enterInputText(action, "warehouse.warehouseName", name[i]);
			common.enterInputText(action, "address.locationAddress1", addr1[i]);
			common.enterInputText(action, "address.locationAddress2", addr2[i]);
			common.enterInputText(action, "address.cityName", city[i]);
			common.enterInputText(action, "address.stateCode", state[i]);
			common.enterInputText(action, "address.zipCode", zip[i]);
			common.enterTextInTextArea("Notes:", notes[i]);
			common.clickFormButton(action, "create");
			Assert.assertEquals("Successfully saved " + name[i] + ".",common.getYukonText("Successfully saved " + name[i] + "."));
		}
	common.end();
	}
	/**
	 * This test logs in as starsop11 and edits the warehouse's name
	 * This test depend on data in input file {@link TestECWarehouseSelenium.xml}
	 */
	@Test
	public void editWarehouse(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name");
		String[] addr1 = getParamStrings("Street Address 1");
		String[] zip = getParamStrings("Zip");
		String[] notes = getParamStrings("Notes");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Warehouses");
		common.clickLinkByName(name[1]);
		Assert.assertEquals(true, common.isTextPresent(name[1]));
		Assert.assertEquals(true, common.isTextPresent(addr1[1]));
		common.clickButtonBySpanText("Edit");
		common.enterInputText(action, "warehouse.warehouseName", name[2]);
		common.enterInputText(action, "address.locationAddress1", addr1[2]);
		common.enterInputText(action, "address.zipCode", zip[2]);
		common.enterTextInTextArea("Notes:", notes[2]);
		common.clickFormButton(action, "update");
		Assert.assertEquals(true, common.isTextPresent(name[2]));
		Assert.assertEquals(true, common.isTextPresent(addr1[2]));
		Assert.assertEquals("Successfully saved " + name[2] + ".", common.getYukonText("Successfully saved " + name[2] + "."));
		common.end();
	}
	/**
	 * This test logs in as starsop11 and deletes the warehouse
	 * This test depend on data in input file {@link TestECWarehouseSelenium.xml}
	 */
	@Test
	public void deleteWarehouse(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = getParamStrings("Name");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Warehouses");
		common.clickLinkByName(name[2]);
		common.clickButtonBySpanText("Edit");
		common.clickButtonBySpanTextWithElementWait("Delete");
		common.clickFormButton(action, "delete");
		Assert.assertEquals("Successfully deleted " + name[2] + ".", common.getYukonText("Successfully deleted " + name[2] + "."));
		common.end();
	}
}
