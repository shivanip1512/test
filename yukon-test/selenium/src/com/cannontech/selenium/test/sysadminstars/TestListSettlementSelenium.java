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
public class TestListSettlementSelenium extends SolventSeleniumTestCase{
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
	 * This test depend on data in input file {@link TestListSettlementSelenium.xml}
	 */
	@Test
	public void addToList(){
		init();
		CommonSolvent common = new CommonSolvent();
		String add = getParamString("Add");
		String option = getParamString("Option");
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Settlement");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonByAttribute("id", "restoreDefaultBtn");
		common.clickFormButton("save", "restoreDefault");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//enters the text into the text box
		common.clickButtonByTitle("Add");
		common.enterInputText("save", "entries[0].text", add);
		//will need to change in next build
		common.enterInputText("save", "entries[0].definitionId", option);
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		Assert.assertEquals("Added settlement not found ", true, common.isTextPresent(add));
		common.clickButtonByTitleWithPageLoadWait("Edit");
		common.clickButtonByTitle("Add");
		common.clickFormButton("save", "save");
		Assert.assertEquals(true, common.isTextPresent("Errors found, check fields."));
		common.end();
		}
	/**
	 * This test logs in and edits the list entry boxes
	 * This test depend on data in input file {@link TestListSettlementSelenium.xml}
	 */
	@Test
	public void editList(){
		init();
		CommonSolvent common = new CommonSolvent();
		String edit = getParamString("Edit");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Settlement");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//entering text into each textbox, according to the xml file
		common.enterInputText("save", "entries[0].text", edit);
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		Assert.assertEquals("Edited settlement not found ", true, common.isTextPresent(edit));
		common.end();
	}
	/**
	 * This test logs in as starsop11 and removes from the list for QA_Test10
	 * This test depend on data in input file {@link TestListSettlementSelenium.xml}
	 */
	@Test
	public void removeFromList(){
		init();
		CommonSolvent common = new CommonSolvent();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		String remove = getParamString("Remove");

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Lists");
		common.clickLinkByName("Settlement");
		common.clickButtonByTitleWithPageLoadWait("Edit");
		//clicking up, down, then remove to remove the list item
		stars.clickMoveUpButton(remove);
		stars.clickMoveDownButton(remove);
		stars.removeListItem(remove);
		common.clickFormButton("save", "save");
		Assert.assertEquals("This list has been updated.", common.getYukonText("This list has been updated."));
		common.end();
	}
}
