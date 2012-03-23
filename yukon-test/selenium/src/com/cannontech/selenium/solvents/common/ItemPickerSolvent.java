/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Solvent handles functions in Item Picker Popup window.
 * @author anuradha.uduwage
 *
 */
public class ItemPickerSolvent extends AbstractSolvent {

	@Override
	public void prepare() {
		//nothing to do at this time.
	}
	
	/**
	 * Method enter query string in item picker popup window.
	 * @param searchString text to be search.
	 * @return
	 */
	public ItemPickerSolvent enterQueryTerm(String searchString) {
		String queryLocator = "//input[contains(@id,'PointPicker_ss')]";
		selenium.waitForElement(queryLocator);
		if(!selenium.isElementPresent(queryLocator))
			throw new SolventSeleniumException("Unable to enter values for 'Query: input at " + queryLocator);
		selenium.typeKeys(queryLocator, searchString);
		selenium.waitForAjax();
		return this;
	}
	
	/**
	 * Click previous arrow to get search resutls.
	 * @return
	 */
	public ItemPickerSolvent clickPrevious() {
		String previous = "//td[@class='prevNavArrow']//img[1]";
		selenium.waitForElement(previous);
		if(!selenium.isElementPresent(previous))
			throw new SolventSeleniumException("Previous Arrow link is not available at " + previous);
		selenium.click(previous);
		return this;
	}
	
	/**
	 * Click next arrow to get search results.
	 * @return
	 */
	public ItemPickerSolvent clickNext() {
		String next = "//td[@class='nextNavArrow']//img[1]";
		selenium.waitForElement(next);
		if(!selenium.isElementPresent(next))
			throw new SolventSeleniumException("Next Arrow link is not available at " + next);
		selenium.click(next);
		return this;
	}
	
	/**
	 * Method to pick items from the list.
	 * @return
	 */
	public ItemPickerSolvent clickSelectDevices() {
		String deviceButton = "//td[@class='right']//input[@value='Select Devices']";
		selenium.waitForElement(deviceButton);
		if(!selenium.isElementPresent(deviceButton))
			throw new SolventSeleniumException("Select Devices button is not available at " + deviceButton);
		selenium.click(deviceButton);
		return this;
	}
	
	/**
	 * Method enters query string in item picker popup window.
	 * @param searchString text to be search.
	 * @return
	 */
	public ItemPickerSolvent selectPoint(String deviceName, String pointName) {
		String pointLocator = "//*[@class='popUpDiv simplePopup pickerDialog']//following::td[normalize-space(text())='" + deviceName + "'][1]//preceding-sibling::td//a[normalize-space(text())='" + pointName + "']";
		selenium.waitForElement(pointLocator);
		if(!selenium.isElementPresent(pointLocator))
			throw new SolventSeleniumException("Unable to click link at " + pointLocator);
		selenium.click(pointLocator);
		return this;
	}	
	
    public void clickPickerButtonAjax(String pickerBase) {
        String pickerButtonXPath = generatePickerButtonPath(pickerBase);
        String pickerPopupHtmlBasePath = generatePickerPopupHtmlBasePath(pickerBase);
        
        selenium.waitForElement(pickerButtonXPath);
        if(!selenium.isElementPresent(pickerButtonXPath)) {
            throw new SolventSeleniumException("Select Devices button is not available at " + pickerButtonXPath);
        }
        selenium.click(pickerButtonXPath);
        selenium.waitForElement(pickerPopupHtmlBasePath, CommonSolvent.PICKER_POPUP_LOAD_TIMEOUT);
    }
    
    /**
     * Click any link on the page by its name. 
     * @param linkName name of the link
     * @return
     */
    public void clickLinkByNameInPicker(String linkName) {
        String linkLocator = "//a[normalize-space(text())='" + linkName + "']";
        selenium.waitForElement(linkLocator);
        if(!selenium.isElementPresent(linkLocator)) {
            throw new SeleniumException("Unalbe to fine Link " + linkName + "check the link String.");
        }
        selenium.click(linkLocator);
    }
    
    private String generatePickerButtonPath(String pickerBase) {
        // TODO Fix Picker Button Structure. 
        // This isn't really a great xpath.  we should be able to use a direct span like above, but software
        // needs to make that change.
        return generatePickerInputAreaPath(pickerBase)+"//..//span//button";
    }
    
    private String generatePickerInputAreaPath(String pickerBase) {
        return "//span[@id='picker_"+pickerBase+"Picker_inputArea']";
    }
    
    private String generatePickerPopupHtmlBasePath(String pickerBase) {
        return "//div[@id='"+pickerBase+"Picker']";
    }

}
