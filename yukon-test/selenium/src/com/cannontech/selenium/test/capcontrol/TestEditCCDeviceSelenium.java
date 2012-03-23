package com.cannontech.selenium.test.capcontrol;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlEditorSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;

/**
 * This class Creates Cap Control Devices, Edit and Delete them. <br> 
 * This test depend on data in input file {@link TestEditCCDeviceSelenium.xml}
 * 
 * @author anjana.manandhar
 *
 */
public class TestEditCCDeviceSelenium extends SolventSeleniumTestCase {

	/**
	 * Create a SubArea, Edit the SubArea and Delete the SubArea.
	 * 
	 */
	@Test
	public void editSubArea() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Area");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "SubArea-100");
		capControlEdit.clickCreateButton();

		capControlEdit.clickByColumnHeader("General");
		capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewSubArea-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();
		common.clickFindAndGo("NewSubArea-100");
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	
	/**
	 * Create a Special Area, Edit the Special Area and Delete the SpecialArea.
	 * 
	 */
	@Test
	public void editSpecialArea() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Special Area");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "SpecialArea-100");
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("General");
	    capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewSpecialArea-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();
		common.clickFindAndGo("NewSpecialArea-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	
	/**
	 * Create Substation, Edit  Substation and Delete  Substation.
	 */
	@Test
	public void editSubstation() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Substation");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "Substation-100");
		capControlEdit.clickCreateButton();

		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewSubstation-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();

		common.clickFindAndGo("NewSubstation-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
			
	}
	
	/**
	 * Create SubBus, Edit SubBus and Delete  SubBus. 
	 */
	@Test
	public void editSubBus() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Substation Bus");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "SubBus-100");
		capControlEdit.clickCreateButton();

		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewSubBus-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();

		common.clickFindAndGo("NewSubBus-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	
	/**
	 * Create Feeder, Edit the Feeder and Delete Feeder.
	 */
	@Test
	public void editFeeder() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Feeder");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "Feeder-100");
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewFeeder-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();

		common.clickFindAndGo("NewFeeder-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	
	/**
	 * Create CBC, Edit CBC and Delete CapBank.
	 */
	@Test
	public void editCapBank() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "CapBank");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "CapBank-100");
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoNameForCaps", "NewCapBank-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();
		common.clickFindAndGo("NewCapBank-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	
	/**
	 * Create CBC, Edit CBC and Delete CBC. 
	 */
	@Test
	public void editCBC() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "CBC");
		
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "CBC-100");
		capControlEdit.clickCreateButton();

		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "NewCBC-100");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();

		common.clickFindAndGo("NewCBC-100");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	/**
	 * Create Regulator, Edit Regulator and Delete Regulator. 
	 */
	@Test
	public void editRegulator() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Regulator");
		
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", "LTC Regulator - 1");
		capControlEdit.clickCreateButton();

		capControlEdit.clickByColumnHeader("General");
        capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "paoName", "New LTC Regulator - 1");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();

		common.clickFindAndGo("New LTC Regulator - 1");
		Assert.assertEquals("Volt Var Management: Search Results",common.getPageTitle());
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
}
