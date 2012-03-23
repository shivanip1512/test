/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.thoughtworks.selenium.SeleniumException;

/**
 * @deprecated
 * Deprecate the class since all the method of the this class are getting moved to 
 * {@link StarsCommonSolvent}
 * @author anuradha.uduwage
 *
 */
public class StarsGeneralSolvent extends AbstractSolvent {
	
	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Click General link from Yukon Operator Page.
	 * @return
	 */
	public StarsGeneralSolvent clickGeneral() {
		String general = "//div[@class='menuOption2']//a[text() = 'General']";
		if(!selenium.isElementPresent(general))
			throw new SeleniumException("Could not find link General to click");
		selenium.click("//div[@class='menuOption2']//a[text() = 'General']");
		return this;
	}
	
	/**
	 * Click any link on the left hand panel with a given link name.
	 * @param linkName name of the link.
	 * @return return an instance of the AbstractSeleniumDriver.
	 */
	public StarsGeneralSolvent clickStarsLeftMenuLink(String linkName) {
		String linkLocator = "//div[@class='menuOption2']//a[normalize-space(text()) = '" + linkName + "']";
		selenium.waitForElement(linkLocator, 10000);
		if(!selenium.isElementPresent(linkLocator, 10000))
			throw new SeleniumException("Could not find link " + linkLocator + " to click.");
		selenium.click(linkLocator);
		return this;
	}
	
	/**
	 * Method select option from drop down menu in stars operation page.
	 * @param searchByType Label name for the search text box (Located on top of Magnifying glass).
	 * @param optionToSelect option to select from drop down menu.
	 * @return
	 */
	public StarsGeneralSolvent selectSearchByTypeOption(String searchByType, String optionToSelect) {
		String selectLocator = "//select/preceding::div[normalize-space(text())='" + searchByType + "']/following::select[1]";
		selenium.waitForElement(selectLocator);
		if(!selenium.isElementPresent(selectLocator))
			throw new SolventSeleniumException("Unable to find select drop drop menu for " + searchByType + " at " + selectLocator);
		selenium.select(selectLocator, optionToSelect);
		return this;
	}
	
	/**
	 * Method click the magnifying glass to select user to edit: under User/Group editor page.
	 * @return
	 */
	public StarsGeneralSolvent clickSelectUserToEditSearch() {
		String searchLocator = "//div[normalize-space(text())='Select user to edit:']/a[1]";
		selenium.waitForElement(searchLocator);
		if(!selenium.isElementPresent(searchLocator))
			throw new SolventSeleniumException("Unable to locate magnifying glass for Select User to Edit: at " + searchLocator);
		selenium.click(searchLocator);
		return this;
	}
	
	/**
	 * Method click the magnifying glass to select group to edit: under User/Group editor page.
	 * @return
	 */
	public StarsGeneralSolvent clickSelectGroupToEditSearch() {
		String searchLocator = "//div[normalize-space(text())='Select group to edit:']/a[1]";
		selenium.waitForElement(searchLocator);
		if(!selenium.isElementPresent(searchLocator))
			throw new SolventSeleniumException("Unable to locate magnifying glass for Select Group to edit: at " + searchLocator);
		selenium.click(searchLocator);
		return this;
	}
	
	/**
	 * Method enter text in the input fields that are placed in-front of magnifying glass search option in Stars Operator page.
	 * @param label label to identify the text field.
	 * @param inputText Text to be entered in the text input field.
	 * @return
	 */
	public StarsGeneralSolvent enterTextToSearchByMagnifyGlass(String label, String inputText) {
		String inputLocator = "//input/preceding::div[normalize-space(text())='" + label + "']/following::input[1]";
		selenium.waitForElement(inputLocator);
		if(!selenium.isElementPresent(inputLocator))
			throw new SolventSeleniumException("Unable to find input field followed by the " + inputText + " at " + inputLocator);
		selenium.type(inputLocator, inputText);
		return this;
	}

}
