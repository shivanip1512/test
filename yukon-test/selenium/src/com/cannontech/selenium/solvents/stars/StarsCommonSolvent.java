/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import org.joda.time.Duration;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Class to hold common methods inside Stars section. Also some pages in stars have only one functionality, in that case we 
 * should use this class to hold those methods.
 * @author anuradha.uduwage
 *
 */
public class StarsCommonSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Method click enroll link based on the given program name.
	 * @param programName name of the program.
	 * @return
	 */
	public StarsCommonSolvent clickEnrollByProgramName(String programName) {
		String enrollLocator = "//td[normalize-space(text())='" + programName + "']/following::a[normalize-space(text())='Enroll'][1]";
		selenium.waitForElement(enrollLocator);
		if(!selenium.isElementPresent(enrollLocator))
			throw new SolventSeleniumException("Unable to find Enroll link for the program " + programName + " at " + enrollLocator);
		selenium.click(enrollLocator);
		return this;
	}

	/**
	 * Method clicks on Unenroll link based on the given program name.
	 * @param programName of the program.
	 * @return
	 */
	public StarsCommonSolvent clickUnenrollByProgramName(String programName) {
		String unenrollLocator = "//td[normalize-space(text())='" + programName + "']/following::a[normalize-space(text())='Unenroll'][1]";
		selenium.waitForElement(unenrollLocator);
		if(!selenium.isElementPresent(unenrollLocator))
			throw new SolventSeleniumException("Unable to find Unenroll link for the program " + programName+ " at " + unenrollLocator);
		selenium.click(unenrollLocator);
		return this;
	}
	/**
	 * Enters specified text in textarea with the given field label name.
	 * @param textArea name of the field that text are to be typed in.
	 * @param notes text to be entered in the Notes or Description textarea in the Create Call page and/or Residence Information page.
	 * @return 
	 */
	public StarsCommonSolvent enterNotesDescription(String textArea, String notes) {
			
		String textInputLocator = "//input/following::td[normalize-space(text())='" +textArea + "']/following::textarea[1]";
		selenium.waitForElement(textInputLocator);
		if(!selenium.isElementPresent(textInputLocator))
			throw new SeleniumException("Unable to find textarea with name '" + textArea + "' to type in '"+ notes + "'");
		selenium.type(textInputLocator, notes);
		return this;
	}
		
	/**
	 * Enters specified text in textarea with the given field label name.
	 * @param textArea name of the field that text are to be typed in.
	 * @param notes text to be entered in the Notes textarea in the Customer Contact section in the Customer Account page.
	 * @return 
	 */
	public StarsCommonSolvent enterCustomerNotes(String textArea, String notes) {
			
		String textInputLocator = "//table[@id='customerContactTable']//input/following::td[normalize-space(text())='" + textArea + "']/following::textarea[1]";
		selenium.waitForElement(textInputLocator);
		if(!selenium.isElementPresent(textInputLocator))
			throw new SeleniumException("Unable to find textarea with name '" + textArea + "' to type in '"+ notes + "'");
		selenium.type(textInputLocator, notes);
		return this;
	}
	
	/**
	 * Enters specified text in textarea with the given field label name.
	 * @param textArea name of the field that text are to be typed in.
	 * @param notes text to be entered in the Notes textarea in the Service Address section in the Customer Account page.
	 * @return 
	 */
	public StarsCommonSolvent enterServiceNotes(String textArea, String notes) {
			
		String textInputLocator = "//table[@id='serviceAddressTable']//input/following::td[normalize-space(text())='" + textArea + "']/following::textarea[1]";
		selenium.waitForElement(textInputLocator);
		if(!selenium.isElementPresent(textInputLocator))
			throw new SeleniumException("Unable to find textarea with name '" + textArea + "' to type in '"+ notes + "'");
		selenium.type(textInputLocator, notes);
		return this;
	}
	
	/**
	 * Enter or Edit username or password for stars accounts.
	 * @param fieldName name of the field.
	 * @param value value to be entered.
	 * @return
	 */
	public StarsCommonSolvent editCustomerUserNameOrPassword(String fieldName, String value) {
		String userFieldLocator = "//td//preceding::div[normalize-space(text())='Change Username']/following::td[normalize-space(text())='" + fieldName + "'][1]/following::input[1]";
		selenium.waitForElement(userFieldLocator);
		if(!selenium.isElementPresent(userFieldLocator))
			throw new SolventSeleniumException("Unable to find input field for the label " + fieldName + " at " + userFieldLocator);
		selenium.type(userFieldLocator,value);
		return this;
	}
	
	/**
	 * Enter current password, new password and confirm password for the customer.
	 * @param fieldName name of the field.
	 * @param value value to be entered in the field.
	 * @return
	 */
	public StarsCommonSolvent editCustomerPasswordFields(String fieldName, String value) {
		String passwordFieldLocator = "//td//preceding::div[normalize-space(text())='Change Password']/following::td[normalize-space(text())='" + fieldName + "'][1]/following::input[1]";
		selenium.waitForElement(passwordFieldLocator);
		if(!selenium.isElementPresent(passwordFieldLocator))
			throw new SolventSeleniumException("Unable to find input field for the label " + fieldName + " at " + passwordFieldLocator);
		selenium.type(passwordFieldLocator,value);
		return this;		
	}

	/**
	 * Click General link from Yukon Operator Page.
	 * @return
	 */
	public StarsCommonSolvent clickGeneral() {
		String general = "//div[@class='menuOption2']//a[text() = 'General']";
		if(!selenium.isElementPresent(general))
			throw new SeleniumException("Could not find link General to click");
		selenium.click("//div[@class='menuOption2']//a[text() = 'General']");
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Click any link on the left hand panel with a given link name.
	 * @param linkName name of the link.
	 * @return return an instance of the AbstractSeleniumDriver.
	 */
	public void clickStarsLeftMenuLink(String linkName) {
		String linkLocator = "//div[@class='menuOption2']//a[normalize-space(text()) = '" + linkName + "']";

		selenium.waitForElement(linkLocator, Duration.standardSeconds(10));
		
		selenium.click(linkLocator);
		selenium.waitForPageToLoad();
	}
	
	/**
	 * Method select option from drop down menu in stars operation page.
	 * @param searchByType Label name for the search text box (Located on top of Magnifying glass).
	 * @param optionToSelect option to select from drop down menu.
	 * @return
	 */
	public StarsCommonSolvent selectSearchByTypeOption(String searchByType, String optionToSelect) {
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
	public StarsCommonSolvent clickSelectUserToEditSearch() {
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
	public StarsCommonSolvent clickSelectGroupToEditSearch() {
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
	public StarsCommonSolvent enterTextToSearchByMagnifyGlass(String label, String inputText) {
		String inputLocator = "//input/preceding::div[normalize-space(text())='" + label + "']/following::input[1]";
		selenium.waitForElement(inputLocator);
		if(!selenium.isElementPresent(inputLocator))
			throw new SolventSeleniumException("Unable to find input field followed by the " + inputText + " at " + inputLocator);
		selenium.type(inputLocator, inputText);
		return this;
	}	
	
	/**
	 * Method clicks the OK button to Delete Login for an Account on the Customer Contact page on the Stars Operator side. 
	 * @param buttonName name of the button
	 * @return
	 */
	public StarsCommonSolvent clickOkButtonToDeleteLogin(String buttonName) {
		String buttonLocator = "//div[@id='confirmDeleteLoginDialog']//input[contains(@value, '" + buttonName + "')] ";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find the button " + buttonName + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this; 
	}
	
	/**
	 * Method clicks the OK button to Delete Account on the Customer Contact page on the Stars Operator side. 
	 * @param buttonName name of the button
	 * @return
	 */
	public StarsCommonSolvent clickOkButtonToDeleteAccount(String buttonName) {
		String buttonLocator = "//div[@id='confirmDeleteDialog']//input[contains(@value, '" + buttonName + "')] ";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find the button " + buttonName + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this; 
	}
	
	/**
	 * Method clicks on the icon to remove the User from the group and Login Group.
	 * This method is common for User/Group Editor's Login Groups and Userss page. 
	 * @param usernameGroup Username or Group to be removed
	 * @return
	 */
	public StarsCommonSolvent removeUserOrGroup(String usernameGroup) {
		String iconLocator = "//td/a[normalize-space(text())='" + usernameGroup + "']//following::input[@name='remove'][1]";
		selenium.waitForElement(iconLocator);
		if(!selenium.isElementPresent(iconLocator))
			throw new SolventSeleniumException("Unable to find the Remove icon " + usernameGroup + " at " + iconLocator);
		selenium.click(iconLocator);
		selenium.waitForPageToLoad();
		return this; 
	}
	
	/**
	 * Method clicks on the icon to remove the Route or the Substation. 
	 * This method is common for removing Routes and Substations in the Substations And Routes page. 
	 * @param routeSubstation Name of Route or Substation to be removed
	 * @return
	 */
	public StarsCommonSolvent removeRouteOrSubstation(String routeSubstation) {
		String iconLocator = "//tr/td[normalize-space(text())='" + routeSubstation + "']//following::input[@class='pointer icon icon_remove'][1]";
		selenium.waitForElement(iconLocator);
		if(!selenium.isElementPresent(iconLocator))
			throw new SolventSeleniumException("Unable to find the Remove icon " + routeSubstation + " at " + iconLocator);
		selenium.click(iconLocator);
		return this;
	}
	/**
	 * Method clicks on the icon to move the List Item up. 
	 * This method is common for moving List Items up in the Energy Company List page 
	 * @param entry The list item to be moved up
	 * @return
	 */
	public StarsCommonSolvent clickMoveUpButton(String entry) {
		String linkLocator = "//tr/td/input[@value='" + entry + "'][1]//following::button[@title='Move Up'][1]";
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(linkLocator))
			throw new SeleniumException("Unable to find Move Up button");
		selenium.click(linkLocator);
		return this;
	}
	/**
	 * Method clicks on the icon to move the List Item down. 
	 * This method is common for moving List Items down in the Energy Company List page 
	 * @param entry The list item to be moved down
	 * @return
	 */
	public StarsCommonSolvent clickMoveDownButton(String entry) {
		String linkLocator = "//tr/td/input[@value='" + entry + "'][1]//following::button[@title='Move Down'][1]";
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(linkLocator))
			throw new SeleniumException("Unable to find Move Down button.");
		selenium.click(linkLocator);
		return this;
	}
	/**
	 * Method clicks on the icon to remove the List Item. 
	 * This method is common for removing list Items in the Energy Company List page 
	 * @param entry The list item to be removed
	 * @return
	 */
	public StarsCommonSolvent removeListItem(String entry) {
		String linkLocator = "//tr/td/input[@value='" + entry + "'][1]//following::button[@title='Remove'][1]";
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(linkLocator))
			throw new SeleniumException("Unable to find Remove button.");
		selenium.click(linkLocator);
		return this;
	}
	
	/**
	 * Method clicks on the List link based on the header.This method is written due to duplicate link name on the Energy Company Lists page.
	 * @param linkHeader Header Name
	 * @param linkName Link Name
	 * @return
	 */
	public StarsCommonSolvent clickLinkByHeaderName(String linkHeader, String linkName) {
		String linkLocator = "//h3[normalize-space(text())='" + linkHeader + "']//following::a[normalize-space(text())='" + linkName + "']";
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Link Header with name" + linkHeader + " is not available at " + linkLocator);
		selenium.click(linkLocator);
		return this;
	}
	
	/**
	 * Method clicks on the links based on Thermostat name. This method is written to click link under Thermostat on the Customer General page. 
	 * @param thermostatName Name of Thermostat
	 * @param linkName Name of the link
	 * @return
	 */
	public StarsCommonSolvent clickLinkByThermostatName(String thermostatName, String linkName) {
		String linkLocator = "//a[normalize-space(text())='" + thermostatName + "']//following::a[normalize-space(text())='" + linkName + "'][1]";
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Unable to find " + linkName + " is not available at " + linkLocator);
		selenium.click(linkLocator);
		return this;
	}
}
