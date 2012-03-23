package com.cannontech.selenium.test.capcontrol;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlEditorSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
import com.cannontech.selenium.solvents.capcontrol.CCFeederTableSolvent;
import com.cannontech.selenium.solvents.capcontrol.CCSubBusTableSolvent;
import com.cannontech.selenium.solvents.capcontrol.CCSubstationInAreaTableSolvent;
import com.cannontech.selenium.solvents.capcontrol.CCSubStationTableSolvent;
import com.cannontech.selenium.solvents.common.ItemPickerSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
/**
 * This class creates Cap Control Devices along with Strategy, attach them. <br> 
 * Attach Points and verify correct devices are attached and displayed correctly on the 4-Tier View <br>
 * This test depends on data in input file {@link TestVerifyFourTierViewSelenium.xml}
 * 
 * @author anjana.manandhar
 *
 */
public class TestVerifyFourTierViewSelenium extends SolventSeleniumTestCase {
	/**
	 * Method logs into Yukon application, create cap control devices and attach each device.<br>
	 * Method uses input file to get devices information {@link TestVerifyFourTierViewSelenium.xml}.<br>
	 */
	@Test
	public void createDevices() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CommonSolvent common = new CommonSolvent();

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
		
		topMenu.clickTopMenuItem("Create", "Strategy");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("strategy1"));
		capControlEdit.clickCreateButton();
		capControlEdit.clickByColumnHeader("Control Strategy Editor");
		common.selectDropDownMenu("editor/cbcBase", "editorForm:cbcStrategy:Control_Method", "Time of Day");
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton(); 
		
		topMenu.clickTopMenuItem("Create", "Strategy");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("strategy2"));
		capControlEdit.clickCreateButton();
		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();
		
		attachDevices();
		common.end();
	}
	
	/**
	 * Method uses the top menu section to locate orphan device section and 
	 * attached the device.<br>
	 * Method get called in from @see {@link TestVerifyFourTierViewSelenium#createAndAttachCCDevices()}
	 */
	public void attachDevices() {
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		topMenu.clickTopMenuItem("Orphans", "Feeders");
		YukonTableSolvent resultsTable = new YukonTableSolvent("tableId=resTable");
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		
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
	 * Method navigates to the 4-Tier page by clicking on SubArea link.<br>
	 * 
	 */
	@Test
	public void viewFourTierPage() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CapControlTableSolvent capTable = new CapControlTableSolvent();
		CommonSolvent common = new CommonSolvent();
		CCSubstationInAreaTableSolvent substationInArea = new CCSubstationInAreaTableSolvent();
		CCSubBusTableSolvent subBusTable = new CCSubBusTableSolvent();
		CCFeederTableSolvent fdrTable = new CCFeederTableSolvent();

		common.clickLinkByName("Volt/Var Management");
		Assert.assertEquals("Volt Var Management",common.getPageTitle());
		capTable.clickaLinkInaRow("SubArea-101");
		Assert.assertEquals("Volt Var Management: Area (SubArea-101)",common.getPageTitle());
		
		capTable.clickaLinkInaRow("Substation-101");
		Assert.assertEquals("Volt Var Management: Substation (Substation-101)",common.getPageTitle());
		
		//Checks the name of the Devices at each level
		substationInArea.isTextPresentInRow("Substation-101");
		subBusTable.isTextPresentInRow("SubBus-101");
		fdrTable.isTextPresentInRow("Feeder-101");
		//capBankTable.isTextPresentInRow("CBC7010-101");
		attachPointSubstation();
	}
	/**
	 * Method attaches point at Substation level on the 4-Tier page.<br>
	 */
	public void attachPointSubstation() {
		CCSubStationTableSolvent substationTable = new CCSubStationTableSolvent();
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		ItemPickerSolvent itemPicker = new ItemPickerSolvent();
		
		common.clickButtonByTitle("Edit");

		//Attach Volt Reduction Point
		// TODO This really should be a full picker, but it's a little unique in that it has a non unique text link.
		PopupMenuSolvent voltReductionControlPointPicker = PickerFactory.createPopupMenuSolvent("substationVoltReductionPoint", PickerType.MultiSelect);
        capEdit.clickVoltReductionSelectPointLink();
		itemPicker.enterQueryTerm("-01");
        itemPicker.selectPoint("Virtual System-01","CalcStatus-01");
		voltReductionControlPointPicker.clickButton("OK");
		
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		attachStrategyPointsSubBus();
	}
	
	/**
	 * Method attaches the strategy to the SubBus level on the 4-Tier page.<br>
	 * Method get called in from @see {@link TestVerifyFourTierViewSelenium#attachDevices()}
	 */
	public void attachStrategyPointsSubBus() {
		CCSubBusTableSolvent subbusTable = new CCSubBusTableSolvent();
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		ItemPickerSolvent itemPicker = new ItemPickerSolvent();
		
		subbusTable.editDeviceOnFourTier("SubBus-101");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
		
		//Attach Strategy
		capEdit.clickByColumnHeader("Control Strategy Setup");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
		common.selectDropDownMenu("editor/cbcBase", "editorForm:outerView:seasons:0:Strategy_Selection", "Strategy-101");
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		common.getYukonExactText("TOD");
		Assert.assertEquals("TOD",common.getYukonText("TOD"));
		subbusTable.editDeviceOnFourTier("SubBus-101");
		
		//Attach Volt Reduction Point
        // TODO This really should be a full picker, but it's a little unique in that it has a non unique text link.
        PopupMenuSolvent substationBusVoltReductionPointPicker = PickerFactory.createPopupMenuSolvent("substationBusVoltReductionPoint", PickerType.MultiSelect);
		capEdit.clickVoltReductionSelectPointLink();
		itemPicker.enterQueryTerm("-01");
		itemPicker.selectPoint("Virtual System-01","CalcStatus-01");
		substationBusVoltReductionPointPicker.clickButton("OK");
		
		//Attach kVar Point
        // TODO This really should be a full picker, but it's a little unique in that it has a non unique text link.
		PopupMenuSolvent subVarPointPicker = PickerFactory.createPopupMenuSolvent("sub_Var_Point", PickerType.MultiSelect);
		capEdit.clickVarSelectPointLink();
		itemPicker.enterQueryTerm("-01");
		itemPicker.selectPoint("Virtual System-01","SubBus kVar");
		subVarPointPicker.clickButton("OK");
		
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		attachStrategyPointsFeeder();
	}
	
	/**
	 * Method attaches the strategy to the Feeder level on the 4-Tier page.<br>
	 * Method get called in from @see {@link TestVerifyFourTierViewSelenium#attachDevices()}
	 */
	public void attachStrategyPointsFeeder() {
		CCFeederTableSolvent fdrTable = new CCFeederTableSolvent();
		CapControlEditorSolvent capEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		ItemPickerSolvent itemPicker = new ItemPickerSolvent();
		
		Assert.assertEquals("Volt Var Management: Substation (Substation-101)", common.getPageTitle());
		fdrTable.editDeviceOnFourTier("Feeder-101");
		
		//Attach Strategy
		capEdit.clickByColumnHeader("Control Strategy Setup");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());
		common.selectDropDownMenu("editor/cbcBase", "editorForm:outerView:seasons:0:Strategy_Selection", "Strategy-102");
		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
		
		Assert.assertEquals("Volt Var Management: Substation (Substation-101)", common.getPageTitle());
		fdrTable.editDeviceOnFourTier("Feeder-101");
		Assert.assertEquals("CapControl Wizard",common.getPageTitle());

		//Attach kVar Point
        // TODO This really should be a full picker, but it's a little unique in that it has a non unique text link.
		PopupMenuSolvent feederVarPointPicker = PickerFactory.createPopupMenuSolvent("feederVarPoint", PickerType.MultiSelect);
		capEdit.clickVarSelectPointLink();
		itemPicker.enterQueryTerm("-01");
		itemPicker.selectPoint("Virtual System-01","Feed1 kVar");
		feederVarPointPicker.clickButton("OK");

		capEdit.clickTopSubmitButton();
		capEdit.clickReturnButton();
	}
	
}
