/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import org.joda.time.Duration;
import org.junit.Assert;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * This class handle common elements in all the demand response table.
 * @author anuradha.uduwage
 *
 */
public class DemandResponseTableSolvent extends YukonTableSolvent {

	/**
	 * Handle common table among all the demand response tables.
	 * @param params
	 */
	public DemandResponseTableSolvent(String... params) {
		super(params);
	}
	
	/**
	 * Click add to favorite star to add an object to favorite list.
	 * @param name name of the object.
	 * @return
	 */
	public DemandResponseTableSolvent addToFavoritesByName(String name) {
		String favLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + name + "']/preceding::img[@title='Add to favorites'][1]";
		waitForJSObject();
		selenium.waitForElement(favLocator);
		if(!selenium.isElementPresent(favLocator))
			throw new SolventSeleniumException("Add to Favorites Star is not available at " + favLocator);
		selenium.click(favLocator);
		return this;
	}
	
	/**
	 * Click filter link in Demand response table headers.
	 * @return
	 */
	public DemandResponseTableSolvent clickFilterIcon() {
		String locator = "//table[@class='boxTable']//a[normalize-space(.)='Filter']";
		waitForJSObject();
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Unable to find Filter link at " + locator);
		selenium.click(locator);
		return this;
	}
	
	/**
	 * Click on items per page link at the bottom of the Demand Response table.
	 * @param numberOfItems string value of the number of items per page ex: 10.
	 * @return
	 */
	public DemandResponseTableSolvent selectItemsPerPage(String numberOfItems) {
		String locator = "////table[@class='boxTable']//a[normalize-space(.)='" + numberOfItems + "']";
		waitForJSObject();
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SolventSeleniumException("Number of items link is not available or it may be already selected.");
		selenium.click(locator);
		return this;
	}
	
	/**
	 * Click column header to sort the table based on the given column header name.
	 * @param columnHeader name of the column header.
	 * @return
	 */
	public DemandResponseTableSolvent sortByColumnHeader(String columnHeader) {
		String columnHeaderLocator = "//*[@id='controlAreaList']//th//a[normalize-space(text())='" + columnHeader + "']";
		waitForJSObject();
		selenium.waitForElement(columnHeaderLocator);
		if(!selenium.isElementPresent(columnHeaderLocator))
			throw new SolventSeleniumException("Column header " + columnHeader + " is not available at " + columnHeaderLocator);
		selenium.click(columnHeaderLocator);
		return this;
	}
	
	/**
	 * Return the value of the state based on the demand response object.
	 * @param drObject
	 * @return
	 */
	public String getDemandResponseState(String drObject) {
		String stateLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']/following::span[1]";
		//waitForJSObject();
		selenium.waitForElement(stateLocator);
		if(!selenium.isElementPresent(stateLocator))
			throw new SolventSeleniumException("Demand Response object " + drObject + " in table Id with " + this.getTableId());
		return selenium.getText(stateLocator);
	}
	
	/**
	 * Click start button based on the demand response object in a table.
	 * @param drObject name of the demand response object.
	 * @return
	 */
	public DemandResponseTableSolvent clickStartButton(String drObject) {
		String enablePlayButton = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']//following::img[contains(@title, 'Start')][1]";
		String disablePlayButton = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']//following::span[contains(@title, 'already fully active.')][1]";
		String shedButton = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']//following::img[contains(@title, 'Shed')][1]";
		//waitForJSObject();
		if(!selenium.getTitle().equalsIgnoreCase("Load Groups")) {
			selenium.waitForElement(enablePlayButton);
			if(!selenium.isElementPresent(enablePlayButton))
				throw new SolventSeleniumException("Start button for Demand Response Object " + drObject + " is not available");
			selenium.click(enablePlayButton);
			selenium.waitForAjax();
		}
		else {
			selenium.waitForElement(shedButton);
			if(!selenium.isElementPresent(shedButton))
				throw new SolventSeleniumException("Start button for Demand Response Object " + drObject + " is not available");
			selenium.click(shedButton);
			selenium.waitForAjax();
		}
		return this;
	}
	
	/**
	 * Click stop button based on the demand response object in a table.
	 * @param drObject name of the demand response object.
	 * @return
	 */
	public DemandResponseTableSolvent clickStopButton(String drObject) {
		//String disablePlayButton = "//*[@id='controlAreaList']//td//a[normalize-space(text())='" + drObject + "']//following::a[2]";
		String disablePlayButton = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']//following::img[contains(@title, 'Stop')][1]";
		String restoreButton = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']//following::img[contains(@title, 'Restore')][1]";
		//waitForJSObject();
		if(!selenium.getTitle().equalsIgnoreCase("Load Groups")) {
			selenium.waitForElement(disablePlayButton);
			if(!selenium.isElementPresent(disablePlayButton))
				throw new SolventSeleniumException("Stop button for Demand Response object " + drObject + " is not available");
			selenium.click(disablePlayButton);
		}
		else {
			selenium.waitForElement(restoreButton);
			if(!selenium.isElementPresent(restoreButton))
				throw new SolventSeleniumException("Stop button for Demand Response object " + drObject + " is not available");
			selenium.click(restoreButton);
		}
		return this;
	}
	
	/**
	 * Method return value in priority based on the name of the DR Object passed into it.
	 * @param drObject name of the DR Object, etc Program name, Scenario name.
	 * @return
	 */
	public String getDemandResponsePriority(String drObject) {
		String priorityLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + drObject + "']/following::td[6]";
		waitForJSObject();
		selenium.waitForElement(priorityLocator);
		if(!selenium.isElementPresent(priorityLocator))
			throw new SolventSeleniumException("Demand Response " + drObject + "is not available in table Id with " + this.getTableId());
		return selenium.getText(priorityLocator);
	}
	
	/**
	 * Helper method to check if the program name is available before moving on to any action with in this table.
	 * @param programName name of the program.
	 * @return return true if program is available return false if not.
	 */
	protected boolean isDrObjectAvailable(String programName) {
		String programLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + programName + "']";
		//waitForJSObject();
		selenium.waitForElement(programLocator);
		if(!selenium.isElementPresent(programLocator))
			return false;
		else 
			return true;
	}	


	
	// NEW ASSERT METHODS //
    /**
     * 
     */
    public void assertEqualsDemandResponseState(String desiredValue, String drObject, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            String demandResponseState = getDemandResponseState(drObject);
            if (demandResponseState.equals(desiredValue)) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The state did not change to the expected value before the timeout.");

    }
    
   /**
     * 
     */
    public void assertEqualsDemandResponseState(String desiredValue, String drObject) {
        assertEqualsDemandResponseState(desiredValue, drObject, Duration.standardSeconds(20));
    }
}
