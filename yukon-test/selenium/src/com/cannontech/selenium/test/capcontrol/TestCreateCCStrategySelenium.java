package com.cannontech.selenium.test.capcontrol;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlEditorSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlStrategyTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;
/**
 * This class tests the creation of Cap Control object Strategy,verifies the Strategy was created, possibly attach 
 * the Strategy to one of the CapControl devices
 * 
 * @author anjana.manandhar
 *
 */
public class TestCreateCCStrategySelenium extends SolventSeleniumTestCase  {
	/**
	 * Creating an array variable 'strategies' and assigning its value to null.
	 */
	String[] strategies = null;
	/**
	 * Method logs into Yukon application 
	 * Creates Cap Control object 'Strategy'.
	 * Method uses input file to get Strategy information {@link TestCreateCCStrategySelenium.xml}
	 */
	@Test
	public void createStrategy() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
			
		topMenu.clickTopMenuItem("Create", "Strategy");
		String[] strategies = getParamStrings("strategies");
		
		for (String strategy : strategies) {
            capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", strategy);
			capControlEdit.clickCreateButton();
			capControlEdit.clickReturnButton();		
			topMenu.clickTopMenuItem("Create", "Strategy");
		}
		capControlEdit.clickReturnButton();
		viewStrategy();
		topMenu.end();
	}
	
	/**
	 * This method validates the Strategy was created in the above createStrategy method.
	 */
	public void viewStrategy() {
		new YukonTopMenuSolvent().clickTopMenuItem("View", "Strategies");
		strategies = getParamStrings("strategies");
		for (String strategy : strategies) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(strategy));
		}
	}
	
	/**
	 * This method edits one of the Strategies that was created in the above createStrategy method.
	 */
	@Test
	public void editStrategy() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlStrategyTableSolvent ccStrategy = new CapControlStrategyTableSolvent();
		
		topMenu.clickTopMenuItem("View", "Strategies");
		ccStrategy.editStrategy("kVarStrategy");
		
		//common.selectDropDownMenuByName("Analysis Interval:" , "1 minute");
		//common.selectDropDownMenuByName("Max Confirm Time:" , "20 minutes");
		capControlEdit.enterInputText("cbcBase.jsf", "editorForm:cbcStrategy:", "Peak_Start_Time", "08:00");
		capControlEdit.enterInputText("cbcBase.jsf", "editorForm:cbcStrategy:", "Peak_Stop_Time", "17:00");

		capControlEdit.clickTopSubmitButton();
		capControlEdit.clickReturnButton();
		topMenu.end();
	}
	
	/**
	 * Delete all the strategies that created.
	 */
	@Test
	public void deleteStrategy() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlStrategyTableSolvent ccStrategy = new CapControlStrategyTableSolvent();

		topMenu.clickTopMenuItem("View", "Strategies");
		strategies = getParamStrings("strategies");
		for (String strategy : strategies) {
			ccStrategy.deleteStrategy(strategy);
		}
		topMenu.end();
	}
}

