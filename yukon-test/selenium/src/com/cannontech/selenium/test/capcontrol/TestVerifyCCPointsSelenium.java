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
 * This class creates CapControl objects such as SubBus, Feeder, CapBank, CBCs. 
 * Verifies the Analog points got created for SubBus and Feeder. 
 * Verifies the Analog and Status points got created for CapBank and CBCs.  
 * @author anjana.manandhar
 */
public class TestVerifyCCPointsSelenium extends SolventSeleniumTestCase {
	/** 
	 * Creating an array variable 'points' and assigning its value to null.
	 */
	String[] points = null;
	
	/**
	 * This method creates a Substation Bus and verifies the Analog point got created. 
	 */
	@Test
	public void verifySubBusPoints() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common= new CommonSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		
		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Substation Bus");
	    capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("substationBus"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.expandAnalogPoint("SubstationBusEditorScrollDiv");
		Assert.assertEquals("Estimated Var Load", common.getYukonText("Estimated Var Load"));
		Assert.assertEquals("Daily Operations", common.getYukonText("Daily Operations"));
		Assert.assertEquals("Power Factor", common.getYukonText("Power Factor"));
		Assert.assertEquals("Estimated Power Factor", common.getYukonText("Estimated Power Factor"));
		common.end();
	}
	
	/**
	 * This method creates a Feeder and verifies the Analog point got created. 
	 */
	@Test
	public void verifyFeederPoints() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "Feeder");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("feeder"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.expandAnalogPoint("SubstationBusEditorScrollDiv");
		Assert.assertEquals("Estimated Var Load", common.getYukonText("Estimated Var Load"));
		Assert.assertEquals("Daily Operations", common.getYukonText("Daily Operations"));
		Assert.assertEquals("Power Factor", common.getYukonText("Power Factor"));
		Assert.assertEquals("Estimated Power Factor", common.getYukonText("Estimated Power Factor"));
		common.end();
	}
	
	/**
	 * This method creates a CapBank and verifies the Analog and Status points got created. 
	 */
	@Test
	public void verifyCapBankPoint() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "CapBank");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("capBank"));
		capControlEdit.clickCreateButton();
		
		capControlEdit.expandAnalogPoint("SubstationBusEditorScrollDiv");
		Assert.assertEquals("OPERATION", common.getYukonText("OPERATION"));
		
		capControlEdit.expandStatusPoint("SubstationBusEditorScrollDiv");
		Assert.assertEquals("BANK STATUS", common.getYukonText("BANK STATUS"));
		common.end();
	}
	
	/**
	 * This method creates a 1-way CBC and verifies the Status point got created. 
	 */
	@Test
	public void verifyCBCPoint() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "CBC");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("CBC"));
		capControlEdit.clickCreateButton();
	
		capControlEdit.expandStatusPoint("CBCCtlEditorScrollDiv");
		Assert.assertEquals("Capacitor Bank State", common.getYukonText("Capacitor Bank State"));
		common.clickLinkByName("Capacitor Bank State");
		Assert.assertEquals("Editor",common.getPageTitle());
		common.clickButtonByExactName("Return");
		common.end();
	}
	/**
	 * creates a regulator and checks the points attached to it
	 */
	@Test
	public void verifyRegulatorPoints(){
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		String[] name = getParamStrings("Regulator Name");
		String[] type = getParamStrings("Regulator Type");
		String[] points;
		
		common.clickLinkByName("Volt/Var Management");
		
		for(int i=0;i<name.length;i++){
			points = getParamStrings(type[i] + " Points");
			topMenu.clickTopMenuItem("Create", "Regulator");
			common.selectDropDownMenu("cbcWizBase.jsf", "wizardForm:VoltageRegulator_Type", type[i]);
			waitFiveSeconds();
			capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", name[i]);
			common.clickButtonByName("Create");
			for(String point : points)
				Assert.assertEquals("point not found ",true, common.isTextPresent(point));
			capControlEdit.clickReturnButton();
		}
		topMenu.clickTopMenuItem("Orphans", "Regulators");
		new CapControlTableSolvent().deleteOrphansInBulk();
		common.end();
	}
	/**
	 * This method creates a 2-way CBC and verifies the Analog, Accumulator and Status points got created. 
	 */
	@Test
	public void verifyTwoWayCBCPoint() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();

		common.clickLinkByName("Volt/Var Management");
		topMenu.clickTopMenuItem("Create", "CBC");
        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", getParamString("TwoWayCBC"));
		common.selectDropDownMenu("editor/cbcWizBase", "wizardForm:CBC_Type", "CBC 7020");
		common.selectDropDownMenu("editor/cbcWizBase", "wizardForm:CBC_Comm_Channel", "a_LSP_Dedicated");
		capControlEdit.clickCreateButton();	
		
		capControlEdit.expandStatusPoint("CBCCtlEditorScrollDiv");
		capControlEdit.expandAnalogPoint("CBCCtlEditorScrollDiv");
		points = getParamStrings("pointName");
		for (String point : points) { 
			Assert.assertEquals(point, common.getYukonExactText(point));
		}				
		common.clickLinkByName("Capacitor Bank State");
		Assert.assertEquals("Editor",common.getPageTitle());
		common.clickButtonByExactName("Return");
		
		common.clickLinkByName("Firmware Version");
		Assert.assertEquals("Editor",common.getPageTitle());
		common.clickButtonByExactName("Return");
		common.end();
	}
}
