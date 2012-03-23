package com.cannontech.selenium.test.capcontrol;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.capcontrol.CapControlEditorSolvent;
import com.cannontech.selenium.solvents.capcontrol.CapControlScheduleTableSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.cannontech.selenium.solvents.common.YukonTopMenuSolvent;

/**
 * This class tests the creation of a CapControl object Schedule,verifies the schedule was created. 
 * Possibly attach the schedule to the CapControl devices. 
 * @author anjana.manandhar
 */

public class TestCreateCCScheduleSelenium extends SolventSeleniumTestCase {
	
	/**
	 * Creating an array variable 'schedules' and assigning its value to null.
	 */
	String[] schedules = null;
	
	/**
	 * Method logs into Yukon application 
	 * Creates Cap Control object 'Schedule'.
	 * Method uses input file to get Schedule information {@link TestCreateCCScheduleSelenium.xml}
	 */
	@Test
	public void createSchedule() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
			
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		topMenu.clickTopMenuItem("Create", "Schedule");
		String[] schedules = getParamStrings("schedules");
		
		for (String schedule : schedules) {
	        capControlEdit.enterInputText("cbcWizBase.jsf", "wizardForm:", "Name", schedule);
			capControlEdit.clickCreateButton();
			capControlEdit.clickReturnButton();
			topMenu.clickTopMenuItem("Create", "Schedule");
		}
		capControlEdit.clickReturnButton();
		viewSchedule();
		capControlEdit.end();
	}
	
	/**
	 *  This method views and verifies the schedule that was created in the createSchedule method. 
	 */
	public void viewSchedule() {
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		topMenu.clickTopMenuItem("View", "Schedules");
		schedules = getParamStrings("schedules");
		for (String schedule : schedules) {
			Assert.assertEquals(true, new YukonTableSolvent().isTextPresentInRow(schedule));
		}
	}
	
	/**
	 * This method edits one of the Schedules that was created in the above createSchedule method.
	 */
	@Test
	public void editSchedule() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		CapControlScheduleTableSolvent ccSchedule = new CapControlScheduleTableSolvent();
		CapControlEditorSolvent capControlEdit = new CapControlEditorSolvent();
		
		topMenu.clickTopMenuItem("View", "Schedules");
		ccSchedule.editSchedule("Schedule-1");
		capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "name", "Schedule-1-Edit");
		capControlEdit.clickTopSubmitButton();
		
		capControlEdit.enterInputText("cbcBase.jsf", "editorForm:general:", "name", "Schedule-1");
		capControlEdit.clickTopSubmitButton();
		
		capControlEdit.clickReturnButton();
		topMenu.end();
	}
	/**
	 *  This method deletes the schedules that are created from the createSchedule method. 
	 */
	@Test
	public void deleteSchedule() {
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		new CommonSolvent().clickLinkByName("Volt/Var Management");
		
		YukonTopMenuSolvent topMenu = new YukonTopMenuSolvent();
		topMenu.clickTopMenuItem("View", "Schedules");
		schedules = null;
		schedules = getParamStrings("schedules");
		CapControlScheduleTableSolvent ccSchedule = new CapControlScheduleTableSolvent();
		for (String schedule : schedules) {
			ccSchedule.deleteSchedule(schedule);
		}
		topMenu.end();
	}
}
