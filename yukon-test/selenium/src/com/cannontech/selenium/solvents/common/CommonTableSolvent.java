/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SeleniumDefaultProperties;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This table solvent class is to handle yukon tables that don't have tableId element.
 * Solvent use the table class element to setup the root level xpath for the table.
 * @author anuradha.uduwage
 *
 */
public class CommonTableSolvent extends AbstractSolvent {

	private String tableClass = null;
	private String cssRoot = null;
	private String xpathRoot = null;
	
	/**
	 * @param params
	 */
	public CommonTableSolvent(String... params) {
		super(params);
		this.tableClass = this.getParams("tableClass");
		this.cssRoot = "css=html."+tableClass+" ";
		this.xpathRoot = getTableCassLocator(tableClass);
	}
	
	/**
	 * Return the table locator for the current table element in the page.
	 * @param tableClass value of the table class element.
	 * @return
	 */
	public static String getTableCassLocator(String tableClass) {
		return "//*[@class='" + tableClass + "']";
	}

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Search and click for a link in a row based on the given link.
	 * @param linkName name of the link.
	 * @return
	 */
	public CommonTableSolvent clickLinkInARow(String linkName) {
		String linkLocator = getXpathRoot() + "//td//a[normalize-space(text())='" + linkName + "']";
		waitForJSObject(2000);
		if(!selenium.isElementPresent(linkLocator))
			throw new SolventSeleniumException("Unable to to find link " + linkName + " at " + linkLocator);
		selenium.click(linkLocator);
		return this;
	}
	
	/**
	 * Method handle the pagination. Based on given items per page, method will click the respective option.
	 * Condition: This method should be used handle items per page where html code only contains table's class.
	 * @param numberOfItems number of items per page.
	 * @return
	 */
	public CommonTableSolvent selectItemsPerPage(String numberOfItems) {
		String itemsPerPageLocator = getXpathRoot() + "//following::td[@class='perPageArea']/a[normalize-space(text())='" + numberOfItems + "']";

		selenium.waitForElement(itemsPerPageLocator);
		selenium.click(itemsPerPageLocator);
		return this;
	}

	/**
	 * Convenient method to get the default timeout for table to load.
	 * @see YukonTableSolvent#waitForJSObject(int) 
	 */
	protected void waitForJSObject() {
		waitForJSObject(Integer.parseInt(SeleniumDefaultProperties.getResourceAsStream("default.yukon.table.reload.timeout")));
	}
	
	/**
	 * Method waits for the JavaScript object tableId to appear in windows.deviceTables array.
	 * @param timeout time to wait in milliseconds.
	 */
	protected void waitForJSObject(int timeout) {
		int time = 0;
		while(time < timeout && !isJSObjectAvailable()) {
			time += 500;
		}
		if(time > timeout) {
			throw new SolventSeleniumException("Time out while looking for tableID " + this.tableClass);
		}
	}
	
	/**
	 * Method confirms if the JavaScript object for this table is present, if not return false.
	 * @return True if the JSObject is present, return false otherwise.
	 */
	protected boolean isJSObjectAvailable() {
		return new Boolean(selenium.getEval("(window.deviceTables && (window.deviceTables['" + this.tableClass + "'] != 'undefined'))"));
	}
	
	/**
	 * @return the xpathRoot
	 */
	public String getXpathRoot() {
		return xpathRoot;
	}

	/**
	 * @param xpathRoot the xpathRoot to set
	 */
	public void setXpathRoot(String xpathRoot) {
		this.xpathRoot = xpathRoot;
	}

	public String getCssRoot() {
	    return cssRoot;
	}
	
	/**
	 * @return the tableClass
	 */
	public String getTableClass() {
		return tableClass;
	}

}
