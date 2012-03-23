/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handle contact information table in stars operator section.
 * @author anuradha.uduwage
 *
 */
public class ContactInfoTableSolvent extends CommonTableSolvent {

	/**
	 * Default Constructor set the table class for contact info table.
	 * @param params
	 */
	public ContactInfoTableSolvent(String... params) {
		super("tableClass=resultsTable contactListTable");
	}
	
	/**
	 * Click a name link in contact info table under stars operator.
	 * @param linkName name of the link.
	 * @return
	 */
	public ContactInfoTableSolvent clickNameLinkByName(String linkName) {
		String linkLocator = getXpathRoot() + "//a[normalize-space(text())='" + linkName + "']";

		selenium.waitForElement(linkLocator);
		selenium.click(linkLocator);
		return this;
	}
	
	/**
	 * Delete contact information for table based on the given contact name.
	 * @param linkName name of the contact.
	 * @return
	 */
	public ContactInfoTableSolvent removeContactInfoByName(String linkName) {
		String linkLocator = getXpathRoot() + "//a[normalize-space(text())='" + linkName + "']/following::img[1]";

		selenium.waitForElement(linkLocator);
		selenium.click(linkLocator);
		return this;		
	}

}
