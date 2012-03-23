/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;

/**
 * This solvent handles SubstationBus table in Cap Control.<br>
 * Class extends the {@link YukonTableSolvent} and sets the table id in constructor.
 * 
 * @author anjana.manandhar
 *
 */
public class CCSubBusTableSolvent extends YukonTableSolvent {
	
	/**
	 * Default constructor sets the table id of the SubstationBus table to override settings in @link {@link YukonTableSolvent}.<br>
	 * @param params
	 */
	public CCSubBusTableSolvent(String... params) {
		super("tableId=subBusTable");

	}
	
	/**
	 * Method clicks the checkbox for a given subbus on the 4 Tier page.
	 * @param subbusName text value of the subbus name.
	 * @return
	 */
	public CCSubBusTableSolvent clickSubBusCheckBox(String subbusName) {
		String buttonLocator = getXpathRoot() + "//td[contains(., '" + subbusName + "')]/preceding::input[contains(@type, 'checkbox')]";
		selenium.waitForElement(buttonLocator, 5000);
		if(!selenium.isElementPresent(buttonLocator, 5000))
			throw new SolventSeleniumException("Checkbox with span " + subbusName + " is not available.");
		selenium.click(buttonLocator);
		return this;
	}
}
