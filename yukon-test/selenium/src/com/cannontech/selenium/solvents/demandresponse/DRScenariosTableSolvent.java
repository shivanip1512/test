/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class handles Demand Response Scenarios table components.
 * @author anuradha.uduwage
 *
 */
public class DRScenariosTableSolvent extends DemandResponseTableSolvent {

	/**
	 * Sets the table id value for the scenarios table.
	 * @param params
	 */
	public DRScenariosTableSolvent(String... params) {
		super("tableId=scenarioList");
	}
	
	/**
	 * Click scenario by name, it throws exception if the scenario can't be found.
	 * @param scenarioName name of the scenario.
	 * @return
	 */
	public DemandResponseTableSolvent clickScenarioByName(String scenarioName) {
		String nameLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + scenarioName + "']";
		//waitForJSObject();
		selenium.waitForElement(nameLocator);
		if(!selenium.isElementPresent(nameLocator))
			throw new SolventSeleniumException("Scenario with the name " + scenarioName + 
					" is not available in scenario table with table ID " + this.getTableId());
		selenium.click(nameLocator);
		selenium.waitForPageToLoad();
		return this;
	}
}
