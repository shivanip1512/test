/**
 * 
 */
package com.cannontech.selenium.solvents.capcontrol;

import org.joda.time.Duration;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.YukonTableSolvent;
import com.thoughtworks.selenium.SeleniumException;



/**
 * Intention of this class is to handle editor page after creating capcontrol
 * element. Example: Area Editor, Feeder Editor etc.
 * <br><br>
 * @author anuradha.uduwage
 *
 */
public class CapControlEditorSolvent extends YukonTableSolvent {

	/**
	 * @param params
	 */
	public CapControlEditorSolvent(String... params) {
		super("tableId=editorForm_tabPane");
	}

	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}	
	
	/**
	 * Click top level submit button in Cap Control Editor area.
	 * @return
	 */
	public CapControlEditorSolvent clickTopSubmitButton() {
		String locator = "//span[@id='hdr_buttons']//input[contains(@value, 'Submit')]";
		selenium.waitForElement(locator, Duration.standardSeconds(9));

		selenium.click(locator);
		selenium.waitForPageToLoad();
		return this;
	}

	/**
	 * Given the Column Header method will Click on the that column header.
	 * @param colHeader Name of the column header.
	 * @return
	 */
	public CapControlEditorSolvent clickByColumnHeader(String colHeader) {
		String locator = this.getTableRowXPathRoot(1) + "//input[@value='" + colHeader + "']";
		selenium.waitForElement(locator);

		selenium.click(locator);
		return this;
	}
	
	/**
	 * Method click the create button in Cap Control JSF pages to <br>
	 * create a device.
	 * @return
	 */
	public CapControlEditorSolvent clickCreateButton() {
		String createXpath = "//table[@id='wizardForm:body']//input[@value='Create']";
		selenium.isVisible(createXpath);
		selenium.waitForElement(createXpath, Duration.standardSeconds(2));

		selenium.click(createXpath);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Click Return button in Cap Control JSF pages.
	 * @return
	 */
	public CapControlEditorSolvent clickReturnButton() {
	    String inputReturn = "//*[@value='Return']";
	    selenium.waitForElement(inputReturn, Duration.standardSeconds(5));

	    String locator = "//table[@id='wizardForm:body']//input[@value='Return']";
		String span = "//*[@id='editorForm:hdr_return_button' and @value='Return']";
		if(selenium.isElementPresent(span, Duration.standardSeconds(3))) {
			selenium.click(span);			
		} else if(selenium.isElementPresent(locator, Duration.standardSeconds(3))) {
			selenium.click(locator);
		}

		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Return boolean value based on the existence of the DB update message.
	 * @return return true if successful message exists.
	 */
	public boolean isDBUpdateSuccessful() {
		String updateMsgLocator = "//*[normalize-space(text())='Database UPDATE successful']";
		boolean isPresent = selenium.isElementPresent(updateMsgLocator, 5000);
		return isPresent;
	}
	
	/**
	 * Based on the device name method attach device to another device by clicking 'Add >>'<br>
	 * and confirms the attachment by looking for '<< Remove' link.
	 * TODO: Remove the control block once the dom structure is fixed.
	 * @param deviceName name of the device to be attached.
	 * @return
	 */
	public CapControlEditorSolvent clickAddToAttachDevice(String deviceName) {
		String addLocator = "//*[@class='scrollerTable']//td[contains(., '" + deviceName + "')]//following-sibling::td/a[text()='Add >>']";
		String removeLocator = "//a[text()='<< Remove']/following::td[text()='" + deviceName + "']";
		//area add link dom is inconsistent with the rest of the add links this is a work around.
		String areaAddLocator = "//*[@class='dataCell']//td[contains(., '" + deviceName + "')]//following-sibling::td/a[text()='Add >>']";
		//this if block should be removed once fixed.
		if(selenium.isElementPresent(areaAddLocator))
			selenium.click(areaAddLocator);
		else {
			if(!selenium.isElementPresent(addLocator))
				throw new SeleniumException("The device " + deviceName + " is not available, check if the device name spelled correctly");
			selenium.click(addLocator);
		}
		//selenium.waitForAjax();
		selenium.waitForElement(removeLocator);
		if(!selenium.isElementPresentAndVisible(removeLocator, 5000))
			throw new SeleniumException("Remove link is not available");
		clickTopSubmitButton();
		clickReturnButton();		
		return this;
	}
	
	
	/**
	 * Based on the device name method attach device to another device by clicking '<<Remove'<br>
	 * and confirms the attachment by looking for 'Add>>' link.
	 * TODO:Extend TableSolvent and implement a class to handle all the options in assignment tables.
	 * @param deviceName name of the device to be attached.
	 * @return
	 */
	public void clickRemoveToDetachDevice(String deviceName) {
		String removeLocator = "//*[@class='scrollerTable']//td[contains(., '" + deviceName + "')]//preceding-sibling::td/a[text()='<< Remove']";
		String removeBigLocator = "//*[@class='bigScrollerTable']//td[contains(., '" + deviceName + "')]//preceding-sibling::td/a[text()='<< Remove']";

		//handle inconsistent dom structure.
		if(selenium.isElementPresent(removeBigLocator)) {
			selenium.click(removeBigLocator);
		} else {
			selenium.waitForElement(removeLocator);
			selenium.click(removeLocator);
		}
		
		selenium.waitForPageToLoad();
	}
	
		
	/**
	 * Expands the Analog Point by clicking on (+) sign. If the expand(+) sign is not available, it will an Exception error.  
	 * @return
	 */
	public CapControlEditorSolvent expandAnalogPoint(String containerId) {

        // This xpath could be a little better.  It's really not that flexible.  This will cause problems if there is an image before the expander in the same tr.tag
	    String analogImgLocator = "//div[@id='"+containerId+"']//span[contains(text(), 'Analog')]/../../td/img[1]";
		selenium.waitForElement(analogImgLocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
		selenium.click(analogImgLocator);
		return this;		
	}
	
	/**
	 * Expands the Status Point by clicking on (+) sign. If the expand(+) sign is not available, it will an Exception error.  
	 * @return
	 */
	public CapControlEditorSolvent expandStatusPoint(String containerId) {

        // This xpath could be a little better.  It's really not that flexible.  This will cause problems if there is an image before the expander in the same tr.tag
		String statusImgLocator = "//div[@id='"+containerId+"']//span[contains(text(), 'Status')]/../../td/img[1]";
		selenium.waitForElement(statusImgLocator, CommonSolvent.WAIT_FOR_ELEMENT_TIMEOUT);
		selenium.click(statusImgLocator);
		return this;
	}
	
	/**
	 * Click Select point link for Volt Reduction Control Point Setup on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public CapControlEditorSolvent clickVoltReductionSelectPointLink() {
		String vrlinkLocator = "//a//preceding::*[normalize-space(text())='No Volt Reduction Point']/preceding::a[text()='Select point'][1]";
		selenium.waitForElement(vrlinkLocator);
		if(!selenium.isElementPresent(vrlinkLocator))
			throw new SeleniumException("nable to find link to select point under Var at " + vrlinkLocator);
		selenium.click(vrlinkLocator);
		return this;
	}
	
	/**
	 * Click Select point link for VAR Point Setup on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public CapControlEditorSolvent clickVarSelectPointLink() {
		String varlinkLocator = "//a//preceding::*[normalize-space(text())='No Var Point']/preceding::a[text()='Select point'][1]";
		selenium.waitForElement(varlinkLocator);
		if(!selenium.isElementPresent(varlinkLocator))
			throw new SeleniumException("Unable to find link to select point under Var at " + varlinkLocator);
		selenium.click(varlinkLocator);
		return this;
	}
	
	/**
	 * Click Select point link for Watt Point Setup on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public CapControlEditorSolvent clickWattSelectPointLink() {
		String wattlinkLocator = "//a//preceding::*[normalize-space(text())='No Watt Point']/preceding::a[text()='Select point'][1]";
		selenium.waitForElement(wattlinkLocator);
		if(!selenium.isElementPresent(wattlinkLocator))
			throw new SeleniumException("Unable to find link to select point under Watt at" + wattlinkLocator);
		selenium.click(wattlinkLocator);
		return this;
	}
	
	/**
	 * Click Select point link for Volt Point Setup on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public CapControlEditorSolvent clickVoltSelectPointLink() {
		String voltlinkLocator = "//a//preceding::*[normalize-space(text())='No Volt Point']/preceding::a[text()='Select point'][1]";
		selenium.waitForElement(voltlinkLocator);
		if(!selenium.isElementPresent(voltlinkLocator))
			throw new SeleniumException("Unable to find link to select point under Volt at" + voltlinkLocator);
		selenium.click(voltlinkLocator);
		return this;
	}

	
	public void enterInputText(String formActionBase, String inputFieldPrefix, String inputFieldNameBase, String text){
	    CommonSolvent common = new CommonSolvent();

	    String inputFieldName = inputFieldPrefix+inputFieldNameBase;
	    common.enterInputText(formActionBase, inputFieldName, text);
	}
	
}
