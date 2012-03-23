package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;
import com.cannontech.selenium.core.locators.LocatorEscapeUtils;
import com.cannontech.selenium.core.locators.XpathLocator;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handles STARS energy company setup.
 * @author anjana.manandhar
 *
 */
public class EnergyCompanySolvent extends CommonTableSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	/**
	 * Constructor that accepts multiple params.
	 * @param params
	 */
	public EnergyCompanySolvent(String... params) {
		super("tableClass=compactResultsTable listTable");
	}	
	
	/**
	 * Method removes the Member Energy Company based on the Company Name.
	 * @param companyName name of the Member Company 
	 * @return
	 */
	public EnergyCompanySolvent removeMemberEnergyCompany(String companyName) {
		Locator nameRemoveLocator = new CssLocator("tr:contains('"+companyName+"') > td button[title='Remove']");

		selenium.waitForElement(nameRemoveLocator);
		selenium.click(nameRemoveLocator);
		return this;
	}
	
	/**
	 * Method removes the Login Group name based on the Program Group Name.
	 * This is the common method to remove both Operator Groups and Residential Customer Groups.
	 * @param groupName name of the Login Group 
	 * @return
	 */
	public void removeLoginGroup(String groupName) {
	    //String escapedGroupName = LocatorEscapeUtils.escapeCss(groupName);
	    
	    // This works because of how sizzle, css locator processor, processes a locator.
	    //Locator groupRowLocator = new CssLocator("tr:contains('" + escapedGroupName + "') > td");
		//Locator nameRemoveLocator = groupRowLocator.append("button[title='Remove']");
	    Locator nameRemoveLocator = new XpathLocator("//tr/td[normalize-space(text())='" + groupName + "']//following::*[@title='Remove'][1]");
	    
		selenium.waitForElement(nameRemoveLocator);
		selenium.click(nameRemoveLocator);
		selenium.waitForPageToLoad();
	}
	
	
	/**
	 * Method clicks on the Status icon to Enable Operator Login.
	 * @param loginName Operator Login Username
	 * @return
	 */
	public EnergyCompanySolvent enableOperatorLogin(String loginName) {
		String iconLocator = "//td//a[normalize-space(text())='" + loginName + "']//following::div/a[@class='icon toggle_status DISABLED'][1]";
		String linkLocator = "//td//a[normalize-space(text())='" + loginName + "']";
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Unable to find the username link at " + linkLocator);
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(iconLocator))
			throw new SolventSeleniumException("Unable to find the Status icon at " + iconLocator);
		selenium.isElementPresent(iconLocator);
		selenium.click(iconLocator);
		return this;
	}
	
	/**
	 * Method clicks on the Status icon to Disable Operator Login.
	 * @param loginName Operator Login Username
	 * @return
	 */
	public EnergyCompanySolvent disableOperatorLogin(String loginName) {
		String iconLocator = "//td//a[normalize-space(text())='" + loginName + "']//following::div/a[@class='icon ENABLED toggle_status'][1]";
		String linkLocator = "//td//a[normalize-space(text())='" + loginName + "']";
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Unable to find the username link at " + linkLocator);
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(iconLocator))
			throw new SolventSeleniumException("Unable to find the Status icon at " + iconLocator);
		selenium.isElementPresent(iconLocator);
		selenium.click(iconLocator);
		return this;
	}
	
	/**
	 * Method verifies the Login button exists for Member Energy Company and clicks on it.
	 * @param memberEnergy Member Energy Company Name
	 * @return
	 */
	public EnergyCompanySolvent loginMemberEnergyCompany(String memberEnergy) {
		String buttonLocator = "//td/a[normalize-space(text())='" + memberEnergy + "']/following::button[@class='pointer hoverableImageContainer formSubmit loginButton'][1]";
		String linkLocator = "//td/a[normalize-space(text())='" + memberEnergy + "']";
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Unable to find the Member Energy Company link at " + linkLocator);
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find the Login button at " + buttonLocator);
		selenium.click(buttonLocator);
		selenium.waitForPageToLoad();
		return this;
	}
}
