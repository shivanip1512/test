/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.AbstractSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Solvent to handle the different Mode and Fan settings on Thermostat - Manual. Any functionality that are specific to 
 * Thermostat - Manual page page should represent in this Solvent.
 * @author anjana.manandhar
 *
 */
public class ThermostatManualPageSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}

	/**
	 * Select the Mode or Fan type on the Thermostat - Manual page.
	 * User should pass in value for MODE or FAN along with the respective option. 
	 * Usage Example: selectModeOrFanType("MODE", "Heat");
	 * @param modeType string value of the mode type.
	 * @return
	 */
	public ThermostatManualPageSolvent selectModeOrFanType(String mode, String modeType) {
		String modeTypeLocator = "//h2[normalize-space(text())='" + mode + "']//following::li[normalize-space(text())='" + modeType + "']";
		selenium.waitForElement(modeTypeLocator);
		if(!selenium.isElementPresent(modeTypeLocator))
			throw new SeleniumException("Could not find Mode type" + modeType + "  to click");
		selenium.clickWithoutWait(modeTypeLocator);
		return this;
	}
}
