/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SeleniumDefaultProperties;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.thoughtworks.selenium.SeleniumException;


/**
 * Solvent that handles all tables in Yukon application.<br>
 * Any table that has a tableId defined can use this class.
 * <br><br>
 * @author anuradha.uduwage
 *
 */
public class YukonTableSolvent extends AbstractSolvent {

	private String tableId = null;
	private String xpathRoot = null;
	
	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Default constructor to extract set by the tableId linklocator.
	 * @param params
	 */
	public YukonTableSolvent(String...params) {
		super(params);
		this.tableId = this.getParams("tableId");
		this.setXpathRoot(getTableLocator(tableId));
	}
	
	/**
	 * Given the row number method will simulate mouse over action on any row.
	 * @param rowNum integer value of the row.
	 * @return
	 */
	public YukonTableSolvent mouseOver(int rowNum) {
		selenium.mouseOver(getTableRowXPathRoot(rowNum));
		return this;
	}
	
	/**
	 * Given the text value method will simulate mouse over action on any row.
	 * @param rowValue string value of the cell in a row.
	 * @return
	 */
	public YukonTableSolvent mouseOver(String rowValue) {
		String locator = getXpathRoot() + "//tr//td[contains(text(), '" + rowValue + "')]";
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SolventSeleniumException("Row with " + rowValue + "doesn't exist in table with tableId " + this.getTableId());
		selenium.mouseOver(locator);
		return this;
	}
	
	/**
	 * Click the row by given row index.
	 * @param rowIndex Index of the row.
	 * @return
	 */
	public YukonTableSolvent clickRowByIndex(int rowIndex) {
		String rowLocator = this.getTableRowXPathRoot(rowIndex);
		selenium.waitForElement(rowLocator);
		if(!selenium.isElementPresent(rowLocator))
			throw new SolventSeleniumException("Row number " + rowIndex + " doesn't exists at " + rowLocator);
		selenium.click(rowLocator);
		return this;
	}
	/**
	 * Based on the index of the row and index of the cell the method will return 
	 * the text of the cell.
	 * 
	 * @param rowInxed index of the row.
	 * @param cellIndex index of the cell.
	 * @return cellText string value of the text in cell.
	 */
	public String getTextInCell(int rowInxed, int cellIndex) {
		String cellLocator = this.getTableRowXPathRoot(rowInxed) + "//td[" + cellIndex +"]";
		selenium.waitForElement(cellLocator, 
				Integer.parseInt(SeleniumDefaultProperties.getResourceAsStream("default.yukon.table.reload.timeout")));
		this.waitForJSObject(2000);
		String cellText = selenium.getText(cellLocator);
		return cellText;
	}
	
	/**
	 * Return a boolean value if the text is present in  a row.
	 * @param text text to find.
	 * @return
	 */
	public boolean isTextPresentInRow(String text) {
		String rowLocator = "//tr[contains(.,'" + text + "')][1]";
		String rowText = null;
		this.waitForJSObject(2000);
		if(!selenium.isElementPresent(rowLocator))
			throw new SolventSeleniumException("Unable to find " + text + " in inside " + this.getTableId());
		rowText = selenium.getText(rowLocator);
		if(rowText != null)
			return true;
		else
			return false;
	}
	/**
	 * Returns the locator for a table element in the current page.
	 * @param tableId The table id to search for.
	 * @return The XPath locator string for a given table id.
	 */
	public static String getTableLocator(String tableId) {
		return "//*[@id='" + tableId + "']";
	}
	
	/**
	 * Construct and return root xpath for the table's header with a given index.
	 * @param index 
	 * @return return the Xpath for column header.
	 */
	public String getTableHeaderColumnXpath(int index) {
		return getTableHeaderXpathRoot() + "[" + index + "]";
	}
	
	/**
	 * Construct and return the root Xpath for the table header.
	 * @return return the xpath.
	 */
	public String getTableHeaderXpathRoot() {
		if(new CommonSolvent().isPageTitle("CapControl Wizard") != true)
			return getTableRowXPathRoot(1) + "//th";
		else
			return getTableRowXPathRoot(1) + "//td";
	}
	
	/**
	 * TODO: Rewrite algorithm to find first content in table.
	 * @param content
	 * @param timeout
	 * @return
	 */
	public int findFirstRowWithContent(String content, int timeout) {
		//*[@id='areaTable']//tr[contains(., 'Area1')]//td/a[@class='editImg'][2]
		String locator = getXpathRoot() + "//tr[contains(., '" + content + "')]";
		String[] xpathes = selenium.explodeXpath(locator);
		int rowIndex = 0;
		if(selenium.isElementPresent(xpathes[0], timeout)) {
			rowIndex = selenium.getXpathCount(xpathes[0] + "").intValue() + 1;
		}
		return rowIndex;
	}
	
	/**
	 * Edit a device in a table using the Pencil edit image.
	 * @param deviceName Name of the device.
	 * @return
	 */
	public YukonTableSolvent editDeviceByDeviceName(String deviceName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + deviceName + "')]//a[@class='tierIconLink'][1]";
		waitForJSObject(2000);
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + deviceName + "in table with tableId " + this.tableId);
		}
		selenium.click(locator);
		return this;
	}
	
	/**
	 * Edit a device in a table using the Pencil edit image from the 4-Tier page.
	 * @param deviceName Name of the device.
	 * @return
	 */
	public YukonTableSolvent editDeviceOnFourTier(String deviceName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + deviceName + "')]//a[@class='simpleLink'][1]";
		waitForJSObject(2000);
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + deviceName + "in table with tableId " + this.tableId);
		}
		selenium.click(locator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Delete a device in a table by clicking on Delete Cross image.
	 * @param deviceName name of the device.
	 * @return
	 */
	public YukonTableSolvent deleteDeviceByDeviceName(String deviceName) {
		String locator = getXpathRoot() + "//tr[contains(., '" + deviceName + "')]//td/a[@class='tierIconLink'][2]";
		waitForJSObject(2000);
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator)) {
			throw new SeleniumException("Unable to find device name " + deviceName + "in table with tableId " + this.tableId);
		}
		selenium.click(locator);
		return this;		
	}
	
	/**
	 * Returns the total row count in a table.  
	 * @return rowCount
	 */
	public int getTableRowCount() {
		return selenium.getXpathCount(this.xpathRoot + "//tr").intValue();
	}
	
	/**
	 * Click a link in side a row within any table.
	 * @param linkName name of the link.
	 * @return
	 */
	public YukonTableSolvent clickaLinkInaRow(String linkName) {
		String linkLocator = "//tr//td//a[normalize-space(text())='" + linkName + "']";
		selenium.waitForElement(linkLocator);
		if(!selenium.isElementPresent(linkLocator))
			throw new SeleniumException("Unable to find the link name with " + linkName + " at " + linkLocator);
		selenium.click(linkLocator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Click a row when a row is not a typical link. This method should be used via the respective object reference.
	 * @param columnValue value of the string.
	 * @return
	 */
	public YukonTableSolvent clickRowByColumnValue(String columnValue) {
		String columnLocator = getXpathRoot() + "//th//following::td[normalize-space(text())='" + columnValue + "']";
		selenium.waitForElement(columnLocator);
		if(!selenium.isElementPresent(columnLocator))
			throw new SolventSeleniumException("Unable to to find value "  + columnValue + " at " + columnLocator);
		selenium.click(columnLocator);
		return this;
	}
	
	/**
	 * Method handle the pagination. Based on given items per page, method will click the respective option.
	 * Condition: This method should be used handle items per page where html code has table id defined.
	 * @param numberOfItems number of items per page.
	 * @return
	 */
	public YukonTableSolvent selectItemsPerPage(String numberOfItems) {
		String itemsPerPageLocator = getXpathRoot() + "//following::td[@class='perPageArea']/a[normalize-space(text())='" + numberOfItems + "']";
		selenium.waitForElement(itemsPerPageLocator);
		if(!selenium.isElementPresent(itemsPerPageLocator))
			throw new SolventSeleniumException("Unable to find items per page option with " + numberOfItems + 
					". May be option has already been selected by default.");
		selenium.click(itemsPerPageLocator);
		return this;
	}
	
	/**
	 * Returns entire row if the row contains given value.
	 * @param valueInRow a string value in row.
	 * @return
	 */
	protected String getFirstRowWithValues(String valueInRow) {
		String rowLocator = getXpathRoot() + "//tr[contains(.,'" + valueInRow + "')]";
		return rowLocator;
	}
	
	/**
	 * Gets the xpath expression for the row with the given index.
	 * @param rowNum integer value of the row.
	 * @return an xpath expression for the row with the index.
	 */
	protected String getTableRowXPathRoot(int rowNum) {
		return getXpathRoot() + "//tr[" + rowNum + "]"; 
	}
	
	/**
	 * Convenient method to get the default timeout for table to load.
	 * @see YukonTableSolvent#waitForJSObject(int) 
	 */
	protected void waitForJSObject() {
		waitForJSObject(Integer.parseInt
				(SeleniumDefaultProperties.getResourceAsStream("default.yukon.table.reload.timeout")));
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
			throw new SolventSeleniumException("Time out while looking for tableID " + this.tableId);
		}
	}
	
	/**
	 * Method confirms if the JavaScript object for this table is present, if not return false.
	 * @return True if the JSObject is present, return false otherwise.
	 */
	protected boolean isJSObjectAvailable() {
		return new Boolean(selenium.getEval
				("(window.deviceTables && (window.deviceTables['" + this.tableId + "'] != 'undefined'))"));
	}

	/**
	 * Return the tableId
	 * @return
	 */
	public String getTableId() {
		return this.tableId;
	}
	
	/**
	 * Method return the xpath root for an element.
	 * @return xpathRoot xpath for the root element.
	 */
	public String getXpathRoot() {
		return xpathRoot;
	}
	public void setXpathRoot(String xpathRoot) {
		this.xpathRoot = xpathRoot;
	}
}
