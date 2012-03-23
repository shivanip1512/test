package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile
 */
public class TestListCoolingSelenium extends SolventSeleniumTestCase{
	/**
	 * logs in as starsop11
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("starsop11","starsop11");
	}
	/**
	 * This test logs in as starsop11 and adds to the default list for QA_Test10 
	 * Restores to the default, then adds the text entries
	 * This test depend on data in input file {@link TestListCoolingSelenium.xml}
	 */
	@Test
	public void addToList(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] add = getParamStrings("Add");
		String init = getParamString("Initial Number of Entries");//this string[] has a length equal to the number of default values

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Cooling System");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonByAttribute("id", "restoreDefaultBtn");
		common.clickFormButton("save", "restoreDefault");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//enters the text into the text box
		for(int i=0;i<add.length;i++){
			common.clickButtonByTitle("Add");
			common.enterInputText("save", "entries[" + (Integer.valueOf(init)+i) + "].text", add[i]);
		}
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		for(int i=0;i<add.length;i++)
			Assert.assertEquals(true, common.isTextPresent(add[i]));
		common.end();
	}
	/**
	 * This test logs in and edits the list entry boxes
	 * This test depend on data in input file {@link TestListCoolingSelenium.xml}
	 */
	@Test
	public void editList(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] edit = getParamStrings("Edit");
		String init = getParamString("Initial Number of Entries");//this string[] has a length equal to the number of default values

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Cooling System");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//entering text into each textbox, according to the xml file
		for(int i=0;i<edit.length;i++){
			common.enterInputText("save", "entries[" + (Integer.valueOf(init)+i) + "].text", edit[i]);
		}
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		for(int i=0;i<edit.length;i++)
			Assert.assertEquals(true, common.isTextPresent(edit[i]));
		common.end();
	}
	/**
	 * This test logs in as starsop11 and removes from the list for QA_Test10 
	 * This test depend on data in input file {@link TestListCoolingSelenium.xml}
	 */
	@Test
	public void removeFromList(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String[] remove = getParamStrings("Remove");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Cooling System");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//clicking up, down, then remove to remove the list item
		for(int i=0;i<remove.length;i++){
			stars.clickMoveUpButton(remove[i]);
			stars.clickMoveDownButton(remove[i]);
			stars.removeListItem(remove[i]);
		}
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		common.end();
	}
}
