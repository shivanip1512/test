/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Class handles functionalities in billing secton of the application.
 * @author anuradha.uduwage
 *
 */
public class BillingSolvent extends AbstractSolvent {
	
	/**
	 * @param params
	 */
	public BillingSolvent(String... params) {
		super(params);
	}
	
	@Override
	public void prepare() {

	}
	
	/**
	 * Change billing settings by given field name and value.
	 * @param changeLocator Name of the field that the value is changing.
	 * @param value text.
	 * @return
	 */
	public BillingSolvent changeBillingSettings(String changeLocator, String value) {
		if(changeLocator.equalsIgnoreCase("File Format:")) {
			String selectLocator = getRootXpath() + 
			"//td[(text()='" + changeLocator + "')]/following::select[@id='fileFormat']";
			if(!selenium.isElementPresent(selectLocator))
				throw new SeleniumException("Unable to find " + selectLocator + "to pick " + value);			
			selenium.select(selectLocator, value);
		}
		else {
			CommonSolvent commonSolvent = new CommonSolvent();
			commonSolvent.enterText(changeLocator, value);
		}
		return this;
	}
	
	/**
	 * Xpath root for the settings table.
	 * @return
	 */
	private String getRootXpath() {
		return "//table[@class='nameValueTable']";
	}
	

}
