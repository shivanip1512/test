package com.cannontech.selenium.test.metering;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;

/**
 * This class tests verifies the ability to create, edit, copy, and delete a Billing Format.
 * 
 * @author anjana.manandhar
 *
 */
public class TestCRUDBillingSetupSelenium extends SolventSeleniumTestCase{
	
	public void init() {
		//Starts the new session
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
	}
	
	/**
	 * Method verifies the ability to Create, Edit, Copy and Delete the Billing Format.
	 */
	@Test
	public void createBillingFormat() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		topMenu.clickTopMenuItem("Billing");
		topMenu.clickTopSubMenuItem("Billing Setup");
		Assert.assertEquals("Billing Formats Setup", common.getPageTitle());	
		
		YukonTableSolvent.getTableLocator("availableFormat");
		common.selectDropDownMenuByAttributeName("availableFormat","SIMPLE_TOU");
		
		common.clickButtonByNameWithPageLoadWait("Create");
		Assert.assertEquals("Billing Format Editor", common.getPageTitle());
		common.enterInputTextByFormId("begin","formatName", "autoTestFormat1");//no form action
		common.enterInputTextByFormId("begin","header", "Top of Bill");//no form action
		common.enterInputTextByFormId("begin","footer", "Bottom of Bill");//no form action
		
		common.clickButtonByName("Add Fields");
		String[] options = {"meterNumber", "paoName", "totalConsumption - reading", "totalConsumption - timestamp", "totalPeakDemand - reading", "totalPeakDemand - timestamp"};
		for(String option : options){
			common.selectDropDownMenuByIdName("availableFields", option);
			common.clickButtonByExactName("Add");
		}
		common.clickButtonByName("Done");
		common.clickButtonByNameWithPageLoadWait("Save");

		Assert.assertEquals("Billing Formats Setup", common.getPageTitle());
		common.end();
	}
	/**
	 * Method verifies the ability to edit the Billing Format.  
	 */
	@Test
	public void editBillingFormat() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		
		topMenu.clickTopMenuItem("Billing");
		topMenu.clickTopSubMenuItem("Billing Setup");
		Assert.assertEquals("Billing Formats Setup", common.getPageTitle());	
		
		common.selectDropDownMenuByAttributeName("availableFormat","autoTestFormat1");
		common.clickButtonByNameWithPageLoadWait("Edit");
		common.enterInputTextByFormId("begin","formatName", "autoTestFormatEdit");
		
		common.clickButtonByNameWithPageLoadWait("Save");
		Assert.assertEquals("Billing Formats Setup", common.getPageTitle());
		common.end();
	}
	/**
	 * Method verifies the ability to copy the Billing Format.  
	 */
	@Test
		public void copyBillingFormat() {
		init();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		topMenu.clickTopMenuItem("Billing");
		topMenu.clickTopSubMenuItem("Billing Setup");
		
		common.selectDropDownMenuByAttributeName("availableFormat","autoTestFormatEdit");
		common.clickButtonByName("Copy");
		common.clickButtonByNameWithPageLoadWait("Save");
		common.selectDropDownMenuByAttributeName("availableFormat","autoTestFormatEdit (copy)");//put here until we can use isSelectDropDownMenuPresent (no form action)

		Assert.assertEquals("Billing Formats Setup", common.getPageTitle());
		common.end();
	}
		/**
		 * Method verifies the ability to delete the Billing Format.  
		 */
	@Test
	public void deleteBillingFormat() {
		init();
		CommonSolvent common = new CommonSolvent();	
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Metering");
		Assert.assertEquals("Metering", common.getPageTitle());
		topMenu.clickTopMenuItem("Billing");
		topMenu.clickTopSubMenuItem("Billing Setup");
		
		common.selectDropDownMenuByAttributeName("availableFormat","autoTestFormatEdit (copy)");
		common.clickButtonByNameWithPageLoadWait("Delete");
		//assert false that the option is there
		common.selectDropDownMenuByAttributeName("availableFormat","autoTestFormatEdit");
		common.clickButtonByNameWithPageLoadWait("Delete");
		//assert false that the option is there
		common.end();
	}
}