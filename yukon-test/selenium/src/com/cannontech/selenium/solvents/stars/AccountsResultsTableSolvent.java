/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handle Accounts Results table in Stars Operator section.
 * @author anuradha.uduwage
 *
 */
public class AccountsResultsTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor setting the table class value.
	 * @param params
	 */
	public AccountsResultsTableSolvent(String... params) {
		super("tableClass=compactResultsTable rowHighlighting");
	}
	
	/**
	 * Method Returns text of the account name based on the account number.
	 * @param accountNumber customer account number.
	 * @return accountName accountName as text.
	 */
	public String getNameByAccountNumber(String accountNumber) {
		String accountLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + accountNumber + "']";
		String accountName = getXpathRoot() + "//td//a[normalize-space(text())='" + accountNumber + "']/following::td[text()][1]";
		selenium.waitForElement(accountLocator);
		if(!selenium.isElementPresent(accountName))
			throw new SolventSeleniumException("Unable to find Account name for account " + accountNumber + " at "  + accountName);
		String accountText = selenium.getText(accountName);
		return accountText;
	}
	
	/**
	 * Method click on account number in starts accounts search results page.
	 * @param accountNumber value of the account number.
	 * @return
	 */
	public AccountsResultsTableSolvent clickAcctLinkByNumber(String accountNumber) {
		String accountLinkLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + accountNumber + "']";

		selenium.waitForElement(accountLinkLocator);
		selenium.click(accountLinkLocator);
		return this;
	}

}
