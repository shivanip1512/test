/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.AbstractSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Solvent to handle Start Contact Inforpage. Any functionality that are specific to contact 
 * info page should represent in this Solvent.
 * @author anuradha.uduwage
 *
 */
public class ContactInfoPageSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * @param text string value for name.
	 */
	public ContactInfoPageSolvent inputFirstName(String text) {
		String locator = "//td[@class='contactLeft']//input[@name='firstName']";
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Could not find input label firstName to type in");
		selenium.type(locator, text);
		return this;
	}
	
	/**
	 * Click update button on contactInfoPage
	 */
	public ContactInfoPageSolvent clickUpdateContact() {
		String buttonLocator = "//table[@class='contactTable']//input[@type='submit' and @name='update']";
		if(!selenium.isElementPresent(buttonLocator))
			throw new SeleniumException("Could not find update button to click");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * select the phone type on contact page.
	 * @param phoneType string value of the phone type.
	 * @return
	 */
	public ContactInfoPageSolvent selectPhoneNumberType(String phoneType) {
		String phoneTypeLocator = "//select[@name='notificationType_1']";
		if(!selenium.isElementPresent(phoneTypeLocator))
			throw new SeleniumException("Could not find Phone Type Drop Down Menu");
		selenium.select(phoneTypeLocator, phoneType);
		return this;
	}
	
	/**
	 * Click any button with a given button name, on the contact page.
	 * @param buttonName Any input type which are buttons
	 * @return
	 */
	public ContactInfoPageSolvent clickInfoPageButton(String buttonName) {
		String buttonLocator = "//input[@type='submit' and @value='" + buttonName + "']";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SeleniumException("Could not find button " + buttonName + " to click");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Click Add notification button on contactInfoPage
	 */
	public ContactInfoPageSolvent clickAddNotificationButton() {
		String buttonLocator = "//table[@class='contactTable']//input[@type='submit' and @name='addNotification']";
		String operatorbuttonLocator = "//input[@id='addNotificationImgInput']";
		if(selenium.isElementPresent(buttonLocator))
			selenium.click(buttonLocator);
		if(selenium.isElementPresent(operatorbuttonLocator))
			selenium.click(operatorbuttonLocator);
		if(!selenium.isElementPresent(buttonLocator)&& !selenium.isElementPresent(operatorbuttonLocator))
			throw new SeleniumException("Could not find the Add notification button to click");
		return this;
	}
	
}
