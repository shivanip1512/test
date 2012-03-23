/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class provide methods to handle functionalities that are available in Inventory Operations page.
 * @author anuradha.uduwage
 *
 */
public class InventoryOperationsSolvent extends AbstractSolvent {

	/**
	 * @param params
	 */
	public InventoryOperationsSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
	
	/**
	 * Method disableds a command schedule on the inventory operations page
	 * @param commandSchedule
	 * @return
	 */
	public InventoryOperationsSolvent disableCommandSchedule(String commandSchedule) {
		String buttonLocator = "//span[contains(text(), '" + commandSchedule + "')]/following::button[contains(@title, 'Click to disable')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
			throw new SolventSeleniumException(commandSchedule + " could not be disabled.");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Method enables a command schedule on the inventory operations page
	 * @param commandSchedule
	 * @return
	 */
	public InventoryOperationsSolvent enableCommandSchedule(String commandSchedule) {
		String buttonLocator = "//span[contains(text(), '" + commandSchedule + "')]/following::button[contains(@title, 'Click to enable')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
			throw new SolventSeleniumException(commandSchedule + " could not be enabled.");
		selenium.click(buttonLocator);
		return this;
	}
		
	/**
	 * Clicks the controls class which hides the inventory popup on click.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonName name of the button.
	 * @return
	 */
	public InventoryOperationsSolvent closeSelectedInventoryPopup() {
		String buttonLocator = "//div[contains(@class, 'controls')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
			throw new SolventSeleniumException("GO Button is not available.");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Removes an inventory configuration task from the inventory operations page.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonName name of the button.
	 * @return
	 */
	public InventoryOperationsSolvent removeInventoryConfigurationTask(String taskName) {
		String buttonLocator = "//a[contains(@title, 'Delete "+taskName+"')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
			throw new SolventSeleniumException(taskName+" is not available to remove.");
		selenium.click(buttonLocator);
		return this;
	}
	
}
