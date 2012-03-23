package com.cannontech.selenium.test.capcontrol;

import junit.framework.Assert;

import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlEditorSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
/**
 * This class test create Cap Control Devices, attach them (attached by work around), <br> 
 * bulk delete operation, and search for devices, and delete.<br> 
 * This test depend on data in input file {@link TestCreateCCDeviceSelenium.xml}
 * 
 * @author anuradha.uduwage
 *
 */
public class TestCreateCCDeviceSelenium extends SolventSeleniumTestCase {
	
	/**
	 * Method logs into Yukon application, create cap control devices and 
	 * attach each device.<br>
	 * Method uses input file to get devices information {@link TestCreateCCDeviceSelenium.xml}
	 */
	@Test
	public void createAndAttachCCDevices() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Area");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("area"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("Substations");
		common.clickLinkByName("Create Substation");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("substation"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("Substation Buses");
		common.clickLinkByName("Create Substation Bus");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("substationBus"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("Feeders");
		common.clickLinkByName("Create Feeder");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("feeder"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickByColumnHeader("CapBanks");
		common.clickLinkByName("Create CapBank");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("capBank"));
		
		common.selectCheckBox("Create New CBC");
		capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Controller_Name", getParamString("CBC"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.clickReturnButton();
		capControlEdit.clickReturnButton();
		capControlEdit.clickReturnButton();
		capControlEdit.clickReturnButton();
		capControlEdit.clickReturnButton();
	
		attachDevices();
		common.end();
	}
	
	/**
	 * Method uses the top menu section to locate orphan device section and 
	 * attached the device.<br>
	 * Method get called in from @see {@link TestCreateCCDeviceSelenium#createAndAttachCCDevices()}
	 */
	public void attachDevices() {
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		YukonTableSolvent resultsTable = new YukonTableSolvent("tableId=resTable");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		
		topMenu.clickTopMenuItem("Orphans", "Feeders");
		resultsTable.editDeviceByDeviceName(getParamString("feeder"));
		capEdit.clickByColumnHeader("CapBanks");
		capEdit.clickAddToAttachDevice(getParamString("capBank"));
		
		topMenu.clickTopMenuItem("Orphans", "Substation Buses");
		resultsTable.editDeviceByDeviceName(getParamString("substationBus"));
		capEdit.clickByColumnHeader("Feeders");
		capEdit.clickAddToAttachDevice(getParamString("feeder"));
		
		topMenu.clickTopMenuItem("Orphans", "Substations");
		resultsTable.editDeviceByDeviceName(getParamString("substation"));
		capEdit.clickByColumnHeader("Substation Buses");
		capEdit.clickAddToAttachDevice(getParamString("substationBus"));

		common.clickFindAndGo(getParamString("area"));
		resultsTable.editDeviceByDeviceName(getParamString("area"));
		capEdit.clickByColumnHeader("Substations");
		capEdit.clickAddToAttachDevice(getParamString("substation"));
	}
	
		/**
	 * Find and delete devices.
	 * Find the Cap Control Device
	 * Delete the Devices without Detaching except for CBC.  
	 */
	@Test
	public void findAndDeleteDevices() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capEditor = new CapControlEditorSolvent();
		CapControlTableSolvent rt = new CapControlTableSolvent();
		CapControlEditorSolvent cs = new CapControlEditorSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		
		common.clickFindAndGo("SubArea-103");
		rt.deleteOrphansInBulk();
		
		common.clickFindAndGo("Substation-103");
		rt.deleteOrphansInBulk();
		
		common.clickFindAndGo("SubBus-103");
		rt.deleteOrphansInBulk();
		
		common.clickFindAndGo("Feeder-103");
		rt.deleteOrphansInBulk();
		
		common.clickFindAndGo("CapBank-103");
		
		rt.editDeviceByDeviceName("CapBank-103");
		common.clickLinkByName("No Control Point");
		cs.clickTopSubmitButton();
		capEditor.clickReturnButton();
		
		common.clickFindAndGo("CBC7010-103");
		rt.deleteOrphansInBulk();
		
		common.clickFindAndGo("CapBank-103");
		rt.deleteOrphansInBulk();
		common.end();
	}
	/**
	 * Find the Cap Control Devices 
	 * Detach Devices 
	 * 
	 */
	@Test
	public void findAndDetachDevices() {
		createAndAttachCCDevices();
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		YukonTableSolvent rt = new YukonTableSolvent("tableId=resTable");
		
		common.clickLinkByName("Volt/Var Management");
		common.clickFindAndGo("SubArea-103");
		rt.editDeviceByDeviceName("SubArea-103");
		capEdit.clickByColumnHeader("Substations");
		capEdit.clickRemoveToDetachDevice(getParamString("substation"));
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		
		common.clickFindAndGo("Substation-103");
		rt.editDeviceByDeviceName("Substation-103");
		capEdit.clickByColumnHeader("Substation Buses");
		capEdit.clickRemoveToDetachDevice(getParamString("substationBus"));
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		
		common.clickFindAndGo("SubBus-103");
		rt.editDeviceByDeviceName("SubBus-103");
		capEdit.clickByColumnHeader("Feeders");
		capEdit.clickRemoveToDetachDevice(getParamString("feeder"));
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		
		common.clickFindAndGo("Feeder-103");
		rt.editDeviceByDeviceName("Feeder-103");
		capEdit.clickByColumnHeader("CapBanks");
		capEdit.clickRemoveToDetachDevice(getParamString("capBank"));
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		//not clicking on CapBank-103 to Remove
		
		common.clickFindAndGo("CapBank-103");
		rt.editDeviceByDeviceName("CapBank-103");
		common.clickLinkByName("No Control Point");
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		common.end();
	}
	
	/**
	 * Click on Orphaned link
	 * Delete orphans as a bulk operation, Substations, SubBuses, Feeders, CapBanks and CBCs
	 */
	@Test
	public void deleteOrphans() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		CapControlTableSolvent rt = new CapControlTableSolvent("tableId=resTable");
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
				
		topMenu.clickTopMenuItem("Orphans", "Substations");
		rt.deleteOrphansInBulk();
		
		topMenu.clickTopMenuItem("Orphans", "Substation Buses");
		rt.deleteOrphansInBulk();	

		topMenu.clickTopMenuItem("Orphans", "Feeders");
		rt.deleteOrphansInBulk();	

		topMenu.clickTopMenuItem("Orphans", "CBCs");
		rt.deleteOrphansInBulk();
		
		topMenu.clickTopMenuItem("Orphans", "Cap Banks");
		rt.deleteOrphansInBulk();
		topMenu.end();
	}
	
	/**
	 * Create a SubArea, Edit the SubArea and Delete the SubArea.
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
	 * creates a regulator and checks the points attached to it
	 */
	@Test
	public void createRegulator(){
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		String[] name = getParamStrings("Regulator Name");
		String[] type = getParamStrings("Regulator Type");
		
		common.clickLinkByName("Volt/Var Management");
		
		for(int i=0;i<name.length;i++){
			topMenu.clickTopMenuItem("Create", "Regulator");
			common.selectDropDownMenu("cbcWizBase.jsf", "wizardForm:VoltageRegulator_Type", type[i]);
			waitFiveSeconds();
			capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", name[i]);
			common.clickButtonByName("Create");
			capControlEdit.clickReturnButton();
		}
		topMenu.clickTopMenuItem("Orphans", "Regulators");
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
}
