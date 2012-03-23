/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This solvent is the location for methods that handle functions in DR Popup windows.
 * @author anuradha.uduwage
 *
 */
public class DRPopupWindowSolvent extends AbstractSolvent {

	/**
	 * Default constructor.
	 * @param params
	 */
	public DRPopupWindowSolvent(String... params) {
		super(params);
	}
	
	@Override
	public void prepare() {
		//nothing to do at this time.
	}
	
	/**
	 * Select Gear from drop down menu in Demand Response popup window.
	 * TODO:We need to address software teams inconsistency on constructing DOM structure to same select combo box. 
	 * @param menuOption option to be select from drop down menu.
	 * @return
	 */
	public DRProgramsTableSolvent selectGear(String menuOption) {
		String gearLocator = "//select[@id='gearNumber']";
		String gearNameLocator = "//select[@name='gearNumber']";
		if(selenium.isElementPresent(gearLocator))
			selenium.select(gearLocator, menuOption);
		if(selenium.isElementPresent(gearNameLocator))
			selenium.select(gearNameLocator, menuOption);
		if(!selenium.isElementPresent(gearLocator) && !selenium.isElementPresent(gearNameLocator))
			throw new SolventSeleniumException("Unable to find the drop down menu for gear at " + gearLocator + " or " + gearNameLocator);
		return new DRProgramsTableSolvent();
	}
	
	/**
	 * This method allow to select shed time from Send Shed popup window
	 * @param shedTime shed time to be select from the drop down menu.
	 * @return
	 */
	public DRPopupWindowSolvent selectShedTime(String shedTime) {
		String shedTimeSelectLocator = "//*[normalize-space(text())='Choose a shed time:']//following-sibling::select[1]";
		selenium.waitForElement(shedTimeSelectLocator);
		if(!selenium.isElementPresent(shedTimeSelectLocator))
			throw new SolventSeleniumException("Unable to find the drop down menu for Shed Time at " + shedTimeSelectLocator);
		selenium.select(shedTimeSelectLocator, shedTime);
		return this;
	}

}
