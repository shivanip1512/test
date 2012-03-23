/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.Duration;
import org.junit.Assert;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.core.locators.CssLocator;
import com.cannontech.selenium.core.locators.Locator;
import com.cannontech.selenium.core.locators.LocatorEscapeUtils;
import com.cannontech.selenium.core.locators.XpathLocator;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Any Common Functionalities that are not specific and available cross 
 * the application should get implemented here.
 * @author anuradha.uduwage
 *
 */
public class CommonSolvent extends AbstractSolvent {

    public static final Duration PAGE_LOAD_TIMEOUT = Duration.standardSeconds(5);
    public static final Duration PICKER_POPUP_LOAD_TIMEOUT = Duration.standardSeconds(5);
    public static final Duration WAIT_FOR_ELEMENT_TIMEOUT = Duration.standardSeconds(5);
    
	/**
	 * Constructor that accepts multiple params.
	 * @param params
	 */
	public CommonSolvent(String... params) {
		super(params);
	}
	
	@Override
	public void prepare() {
		getSeleniumDriver().waitForPageToLoad();
	}
	
	/**
	 * Method click Build Index link to build the the index based on the index name.
	 * @param indexName name of the index.
	 */
	public void buildIndexBuyIndexName(String indexName) {
		Locator indexPathLocator = new CssLocator("tr:contains('"+indexName+"')");
		Locator buildIndexLinkLocator = indexPathLocator.append("a:contains('Build Index')");
		
		selenium.waitForElement(indexPathLocator);
		selenium.click(buildIndexLinkLocator);
		
		selenium.waitForAjax();
		Locator pickerPercentCompleteLocator = new CssLocator("#"+indexName+"percentComplete");
		selenium.waitForElementToBecomeInvisible(pickerPercentCompleteLocator, Duration.standardMinutes(1));
	}
	
	/**
	 * This method will return the title of the page if html title tag
	 * is used. 
	 * @return page title.
	 */
	public String getPageTitle() {
	    Locator titlePath = new CssLocator("title");
	    
	    selenium.waitForElement(titlePath);
	    return selenium.getTitle();
	}
	
	/**
	 * Helper method to simplify JUnit assertion test. 
	 * @param expected Exact string value of the page title. 
	 * @return boolean
	 */
	public boolean isPageTitle(String expected) {
		String expectedTitle = getPageTitle();
		if(expectedTitle.equalsIgnoreCase(expected))
			return true;
		else return false;
	}
	
	/**
	 * Method to used in AssertEquals, to check if we are in the correct page after 
	 * a navigation task. This method should work most of the time, we will have
	 * specific getText method for texts in tables (Cell base approach).  
	 * @param text String to confirm.
	 * @return
	 */
	public String getYukonText(String text) {
	    // In this case the XpathLocator is actually more efficient then the CssLocator.
		Locator txtLocator = new XpathLocator("//*[contains(text(), '" +text+"')]");
//		 Locator txtLocator = new CssLocator("*:contains('"+text+"'):last");
		
		selenium.waitForElement(txtLocator, Duration.standardSeconds(9));
		String foundText = selenium.getText(txtLocator);
		return foundText;
	}
	
	/**
	 * Method verifies if the text is present.
	 * @param text text to verify.
	 * @return true if text is present otherwise false.
	 */
	public boolean isTextPresent(String text) {
		return isTextPresent(text, Duration.standardSeconds(5));
	}
	
	/**
	 * Method verifies if the text is present in the page and it looks for 
	 * the text until the time runs out.
	 * @param text text to verify.
	 * @param wait milliseconds to wait for text.
	 * @return true if text is present otherwise false.
	 */
	private boolean isTextPresent(String text, Duration wait) {
	    // In this case the XpathLocator is actually more efficient then the CssLocator.
	     Locator txtLocator = new CssLocator("body:contains('"+text+"')");
//        Locator txtLocator = new XpathLocator("//*[contains(text(), '"+text+"')]");
        
		String foundText = null;
		try {
			selenium.waitForElement(txtLocator, wait);
			foundText = selenium.getText(txtLocator);
		} catch (SeleniumException e) {
			e.printStackTrace();
		}
		
		return foundText != null;
	}
	
	/**
	 * Match the exact text. This method should be used in page where we need to confirm the 
	 * existance of two similar text strings. Example: "Substation", and "Create Substation". 
	 * @param text String to confirm.
	 * @return
	 */
	public String getYukonExactText(String text) {
		String txtLocator = "//*[text()='" + text + "']";
		
		selenium.waitForElement(txtLocator, Duration.standardSeconds(9));
		String foundText = selenium.getText(txtLocator);
		return foundText;
	}
	/**
	 * Enters specified text in edit box with the given field label name.
	 * 
	 * @deprecated this should be directly connected to a form.  see the form
	 * methods for a good example. EX.: CommonSolvent.enterInputText()
	 * 
	 * @param inputField name of the field that text are to be typed in.
	 * @param text the text to be entered.
	 * @return CommonSolvent
	 */
	@Deprecated
	public CommonSolvent enterText(String inputField, String text) {
		
		String inputLocator = "//input/following::td[normalize-space(text())='" + inputField + "']/following::input[1]";
		String ccInputLocator = "//input/preceding::label[normalize-space(text())='" + inputField + "']/following::input[1]";
		String starsInputLocator = "//input//following::div[(text()='" + inputField + "')]/following::input[1]";
		String tdInput = "//td[normalize-space(text())='" + inputField + "']//following::input[1]";
		String inputUsername = "//*[@id='USERNAME']";

		if(selenium.isElementPresent(tdInput)) {
			selenium.type(tdInput, text);		
		} else {
	
			if(selenium.isElementPresent(inputUsername)) {
				selenium.type(inputUsername, text);
			}
	
			//stars contact page text fields are done in a different way than the rest of the text fields in the app.
			if(selenium.isElementPresentAndVisible(starsInputLocator, 3000)) {
				selenium.type(starsInputLocator, text);
			}
			
			if(isPageTitle("CapControl Wizard") != true && !selenium.isElementPresent(starsInputLocator)) {
				if(!selenium.isElementPresentAndVisible(inputLocator, 3000))
					throw new SeleniumException("Unable to find input field name with '" + inputField + "' to type in '"+ text + "'");
				selenium.type(inputLocator, text);
			} else {
				if(!selenium.isElementPresent(starsInputLocator)) {
					if(!selenium.isElementPresentAndVisible(ccInputLocator, 3000))
						throw new SeleniumException("Unable to find input field name with label name " + inputField + "' to type in '" + text + "'");
					selenium.type(ccInputLocator, text);
				}
			}
		}
		
		return this;
	}

	/**
	 * Method type text in text area, since text areas are different from text fields. 
	 * @param inputField name of the text area field.
	 * @param text value to be entered in the text area.
	 * @return
	 */
	public CommonSolvent enterTextInTextArea(String inputField, String text) {
		String txtAreaLocator = "//td[normalize-space(text())='" + inputField + "']//following::textarea[1]";
		if(!selenium.isElementPresent(txtAreaLocator))
			throw new SolventSeleniumException("Unable to find the text area with the input field " + inputField + " at " + txtAreaLocator);
		selenium.type(txtAreaLocator, text);
		return this;
	}

	/**
	 * Click any input/button based on its name. This method also supports button that doesn't have value attribute properly defined.
	 * Method will wait for button to appear if it doesn't appear then throw exception. 
	 * @param buttonName name of the input.
	 * @return
	 */
	public CommonSolvent clickButtonByName(String buttonName) {
		String buttonLocator = "//input[contains(@value, '" + buttonName + "')]";
		String nonValueButtonLocator = "//button[contains(text(), '" + buttonName + "')]";
		
		if(selenium.isElementPresent(buttonLocator)) {
			selenium.waitForElementToBecomeVisible(buttonLocator, 9000);
			selenium.click(buttonLocator);
		} else if(selenium.isElementPresent(nonValueButtonLocator)) {
			selenium.waitForElementToBecomeVisible(nonValueButtonLocator, 9000);
			selenium.click(nonValueButtonLocator);
		} else {
			if(!selenium.isElementPresentAndVisible(buttonLocator, 9000) && !selenium.isElementPresentAndVisible(nonValueButtonLocator, 9000))
				throw new SeleniumException("Unable to find '" + buttonName +"' to click at " + buttonLocator + " & waited 2000ms");
		}
		return this;
	}

	/**
	 * This method is an extension of the clickButtonByName which uses a waitForPageToLoad when it's done.
     */
    public CommonSolvent clickButtonByNameWithPageLoadWait(String buttonName) {
        clickButtonByName(buttonName);
        selenium.waitForPageToLoad();
        return this;
    }

    /**
     * Click any input/button based on its name. This method also supports button that doesn't have value attribute properly defined.
     * Method will wait for button to appear if it doesn't appear then throw exception. 
     * @param buttonName name of the input.
     * @return
     */
    public CommonSolvent clickButtonByNameWithElementWait(String buttonName, String waitingElementLocator, int wait) {
        String buttonLocator = "//input[contains(@value, '" + buttonName + "')]";
        String nonValueButtonLocator = "//button[contains(text(), '" + buttonName + "')]";
        if(selenium.isElementPresent(buttonLocator)) {
            selenium.waitForElementToBecomeVisible(buttonLocator, 9000);
            selenium.click(buttonLocator);
        }
        else if(selenium.isElementPresent(nonValueButtonLocator)) {
            selenium.waitForElementToBecomeVisible(nonValueButtonLocator, 9000);
            selenium.click(nonValueButtonLocator);
        }
        else {
            if(!selenium.isElementPresentAndVisible(buttonLocator, 9000) && !selenium.isElementPresentAndVisible(nonValueButtonLocator, 9000))
                throw new SeleniumException("Unable to find '" + buttonName +"' to click at " + buttonLocator + " & waited 2000ms");
        }
        
        selenium.waitForElement(waitingElementLocator, wait);
        return this;
    }

    public CommonSolvent clickButtonByNameWithElementWait(String buttonName, String waitingElementLocator) {
        return clickButtonByNameWithElementWait(buttonName, waitingElementLocator, 10000);
    }
    
	
	/**
	 * This method will click the button if the given button name is exact to the available button in page.<br>
	 * Example: Page has two buttons, Add and Add Fields. Using {@link CommonSolvent#clickButtonByName(String)} method will throw an error
	 * since that method looks for a match any word within the given string. 
	 * @param exactButtonName exact string value of the button (name of the button).
	 * @return
	 */
	public CommonSolvent clickButtonByExactName(String exactButtonName) {
		String buttonLocator = "//input[@value='" + exactButtonName + "'][1]";
		selenium.isElementPresent(buttonLocator);
		selenium.waitForElementToBecomeVisible(buttonLocator, 9000);
		if(!selenium.isElementPresentAndVisible(buttonLocator, 9000))
			throw new SeleniumException("Unable to find '" + exactButtonName +"' to click at " + buttonLocator + " & waited 2000ms");
		selenium.click(buttonLocator);
		return this;		
	}
	
	/**
	 * This method handle buttons in Stars Operator section since those button doesn't use HTML input element. 
	 * @param buttonName name of the button.
	 * @return
	 */
	public CommonSolvent clickStarsButtonByName(String buttonName) {
		String button = "//button[normalize-space(text())='" + buttonName + "'][1]";
		selenium.waitForElement(button);
		selenium.click(button);
		return this;
	}

    public void clickStarsButtonByNameWithPageLoad(String buttonName) {
        clickStarsButtonByName(buttonName);
        selenium.waitForPageToLoad();
    }

	
	/**
	 * This method will click button based on the title of the button.This is a new +Add button that is 
	 * added to Stars Operator and Cap Control application.
	 * @param buttonTitle title of the button
	 * @return
	 */
	public CommonSolvent clickButtonByTitle(String buttonTitle) {
		String buttonLocator = "//button[(@title = '" + buttonTitle + "')]";
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find the button with button type" + buttonTitle + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this;
	}
	
	public void clickButtonByTitleWithPageLoadWait(String buttonTitle) {
		clickButtonByTitle(buttonTitle);
		selenium.waitForPageToLoad();
	}
	
	/**
	 * Method clicks the button based on the given attribute name and its value.
	 * <b>Example:<b><i> button id=button001 name=testButton </i><br>
	 * Use the name attribute as best practice since id can be a dynamically generated id.<br>
	 * @param attribute name of the attribute.
	 * @param value text value of the attribute.
	 * @return
	 */
	public CommonSolvent clickButtonByAttribute(String attribute, String value) {
		String buttonLocator = "//button[contains(@" + attribute + ", '" + value + "')]";
		String inputLocator = "//input[contains(@" + attribute + ",'" + value + "')][1]";
		
		if(selenium.isElementPresent(buttonLocator)) {
			selenium.waitForElementToBecomeVisible(buttonLocator, 5000);
			selenium.click(buttonLocator);
		} else if(selenium.isElementPresent(inputLocator)) {
			selenium.waitForElementToBecomeVisible(inputLocator, 5000);
			selenium.click(inputLocator);
		} else {
		    if(!selenium.isElementPresentAndVisible(buttonLocator, 5000) && 
		       !selenium.isElementPresentAndVisible(inputLocator, 5000)) {
		        throw new SolventSeleniumException("Button with attribute " + attribute + " is not available with value " + value);
		    }
		}
		return this;
	}
	
    public void clickButtonByAttributeWithPageLoad(String attribute, String value) {
        clickButtonByAttribute(attribute, value);
        selenium.waitForPageToLoad();
    }

	
	/**
	 * Method clicks the button based on the value of the span html tag used in the button.
	 * 
	 * @deprecated Use clickFormButton when possible.
	 * 
	 * @param spanTextValue text value of the span html tag inside the button.
	 * @return
	 */
	@Deprecated
	public CommonSolvent clickButtonBySpanText(String spanTextValue) {
		String buttonLocator = "//span[contains(text(), '" + spanTextValue + "')]/ancestor::button[1]";
//	    Locator buttonLocator = new CssLocator("span:contains('"+spanTextValue+"') > button");

	    selenium.waitForElement(buttonLocator);
	    selenium.click(buttonLocator);
	    selenium.waitForPageToLoad();
	    return this;
	}

	   /**
     * Method clicks the button based on the value of the span html tag used in the button.
     * @param spanTextValue text value of the span html tag inside the button.
     * @return
     */
    public CommonSolvent clickButtonBySpanTextWithAjaxWait(String spanTextValue) {
        String buttonLocator = "//span[contains(text(), '" + spanTextValue + "')]/ancestor::button[1]";
        selenium.waitForElement(buttonLocator);

        selenium.click(buttonLocator);
        selenium.waitForAjax();
        return this;
        
    }

	
	/**
	 * This should only be used in a case where the picker popup is not programmed correctly
	 */
    @Deprecated
    public void clickButtonBySpanTextWithElementWait(String spanTextValue, String waitingElementLocator) {
        String buttonLocator = "//following::span[contains(text(), '"+ spanTextValue + "')]/ancestor::button[1]";

        selenium.waitForElement(buttonLocator);

        selenium.click(buttonLocator);
        selenium.waitForElement(waitingElementLocator);
    }
    
    /**
     * Any kind of click event should be paired with a wait: waitForPageToLoad, waitForElement, etc.
     * 
     * @deprecated Use the clickFormButton instead of this method.
     * 
     * @param spanTextValue
     */
    @Deprecated
    public void clickButtonBySpanTextWithElementWait(String spanTextValue) {
        String buttonLocator = "//following::span[contains(text(), '"+ spanTextValue + "')]/ancestor::button[1]";

        selenium.waitForElement(buttonLocator);
        selenium.click(buttonLocator);
    }
	
	public CommonSolvent clickButtonByText(String buttonTxt) {
		String buttonLocator = "//following::button[contains(text(), '" + buttonTxt + "')]";
		selenium.waitForElementToBecomeVisible(buttonLocator, 5000);

		selenium.click(buttonLocator);
		selenium.waitForAjax();
		return this;		
	}
	
	/**
	 * Method to enter url from anywhere of the application. 
	 * TODO: get home url from the server settings and add function to navigate to home.
	 * @param url exact web address of the location.
	 * @return
	 */
	public CommonSolvent openURL(String url) {
		selenium.open(url);
		selenium.waitForPageToLoad(9000);
		return this;
	}
	
	/**
	 * Yukon application is so inconsistent so if there is an area that we can't use the 
	 * {@link #enterText(String, String)} use this method in such localized area to customize.
	 *
	 * @deprecated you should use the enterInputText method.
	 *
	 * @param id id value of the input
	 * @param text value
	 * @return
	 */
	@Deprecated
	public CommonSolvent inputTextById(String id, String text) {
		String locator = "//input[@id='"+ id +"']";
		selenium.waitForElement(locator);
		if(!selenium.isElementPresent(locator))
			throw new SeleniumException("Can not find input with id='" + id + "' to enter text");
		selenium.type(locator, text);
		return this;
	}
	
	/**
	 * Select check box by its label name.
	 * @param chkBoxLabel name of the check box.
	 * @return
	 */
	public CommonSolvent selectCheckBox(String chkBoxLabel) {
		String labelLocator = "//input[@type='checkbox']/following-sibling::label[normalize-space(text())='" + chkBoxLabel + "']/preceding::input[1]";
		String spanLocator = "//input[@type='checkbox']/following-sibling::span[normalize-space(text())='" + chkBoxLabel + "']/preceding::input[1]";
		String drCheckBox = "//label[normalize-space(text())='" + chkBoxLabel  + "']//preceding::input[@type='checkbox'][1]";
		String optOutCheckBox = "//td[@title='" + chkBoxLabel + "']//input[@type='checkbox'][1]";
		String anotherOptOut = "//input[@type='checkbox']/following::td[normalize-space(text())='" + chkBoxLabel + "']/preceding::input[1]";
		String avoidHiddenInput = "//input//following::label[normalize-space(text())='" + chkBoxLabel + "']//preceding::input[@type='checkbox'][1]";
		boolean isPresent = false;
		if(selenium.isElementPresent(avoidHiddenInput) && !isPresent) {
			selenium.click(avoidHiddenInput);
			isPresent = true;
		}
		else if(selenium.isElementPresent(anotherOptOut) && !isPresent) {
			selenium.click(anotherOptOut);
			isPresent = true;
		}
		else if(selenium.isElementPresent(optOutCheckBox) && !isPresent) {
			selenium.click(optOutCheckBox);
			isPresent = true;
		}
		else if(selenium.isElementPresent(drCheckBox) && !isPresent) {
			selenium.click(drCheckBox);
			isPresent = true;
		}
		else if(selenium.isElementPresent(labelLocator) && !isPresent) {
            selenium.click(labelLocator);
            isPresent = true;
        }
		else if(selenium.isElementPresent(spanLocator) && !isPresent) {
            selenium.click(spanLocator);
            isPresent = true;
        }
        if(!selenium.isElementPresent(labelLocator, 2000) && !selenium.isElementPresent(spanLocator) && !selenium.isElementPresent(drCheckBox) && !selenium.isElementPresent(optOutCheckBox) && !selenium.isElementPresent(anotherOptOut))
            throw new SeleniumException("Can not find checkbox with the label " + chkBoxLabel + "at " 
            		+ labelLocator + " or " + spanLocator + " or " + drCheckBox + " or " + optOutCheckBox + " or " + avoidHiddenInput);
        selenium.pause(2000);
        return this;
	}

	/**
	 * Click any link on the page by its name. 
	 * @param linkName name of the link
	 * @return
	 */
	public CommonSolvent clickLinkByName(String linkName) {
	    Locator linkLocator = new XpathLocator("//a[normalize-space(text())='"+linkName+"']");
//	    String escapedLink = LocatorEscapeUtils.escapeCss(linkName);
//	    Locator linkLocator = new CssLocator("a:contains('"+escapedLink+"')");

		selenium.waitForElement(linkLocator);
		selenium.click(linkLocator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
     * @deprecated This should be replaced by some form of picker click. 
     * @param linkName name of the link
     * @return
     */
	@Deprecated
    public CommonSolvent clickLinkByNameWithoutPageLoadWait(String linkName) {
        String linkLocator = "//a[normalize-space(text())='" + linkName + "']";

        selenium.waitForElement(linkLocator);
        selenium.click(linkLocator);
        return this;
    }
	
	/**
	 * Method clicks a link by its following span text. This method should be used if the link has span text defined.
	 * @param spanText texts inside span tags.
	 * @return
	 */
	public CommonSolvent clickLinkBySpanText(String spanText) {
		String linkLocator = "//span[contains(text(), '" + spanText + "')]/ancestor::a[1]";

		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(linkLocator, 5000);
		selenium.waitForAjax();
		selenium.click(linkLocator);
		return this;
	}	
	
	/**
	 * Enter text in the Find textbox and click on GO button
	 * @param enterText searchable text.
	 * @return
	 */
	public CommonSolvent clickFindAndGo(String enterText) {
		String searchTextField = "//input/preceding-sibling::input[@type='text']";
		String buttonGo = "//div[@id='findForm']/input[@name='Go']";

		selenium.waitForElement(searchTextField);
		
		selenium.type(searchTextField, enterText);
		if(!selenium.isElementPresent(buttonGo)) {
			throw new SeleniumException("Go button is not avaialbe.");
		}

		selenium.click(buttonGo);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Method selects a option from a drop down menu based on the given option.
	 * example: if the name of select box is Group Type: and in side select box options are Basic and Composed
	 * method can be used as selectDroupDownMenuByName("Group Type:", "Basic");
	 * 
	 * @deprecated The selectDropDownMenu should be used instead.
	 * 
	 * @param labelName name of the drop down menu.
	 * @param menuOption Option to be select.
	 * @return
	 */
	@Deprecated
	public CommonSolvent selectDropDownMenuByName(String labelName, String menuOption) {
	    String menuLocator = "//*[normalize-space(text())='" + labelName + "']//following::select[1]";

		selenium.waitForElement(menuLocator);
		selenium.select(menuLocator, menuOption);
		return this;
	}

    /**
     * Method selects a option from a drop down menu based on the given option.
     * example: if the name of select box is Group Type: and in side select box options are Basic and Composed
     * method can be used as selectDroupDownMenuByName("Group Type:", "Basic");
     * 
     * @deprecated The selectDropDownMenuWithPageLoad should be used instead.
     *
     * @param labelName name of the drop down menu.
     * @param menuOption Option to be select.
     * @return
     */
     @Deprecated
    public CommonSolvent selectDropDownMenuByNameWithRedirect(String labelName, String menuOption) {
        String menuLocator = "//*[normalize-space(text())='" + labelName + "']//following::select[1]";

        selenium.waitForElement(menuLocator, 9000);
        selenium.select(menuLocator, menuOption);
        selenium.waitForPageToLoad();
        return this;
    }
	
	/**
	 * Method selects a option from a drop down menu based on the given option. This method identify the drop down menu 
	 * based on the name attribute given in the select tag in html dom structure.
	 * <b>example: </b> <i> name="ruleType" </i> this value should be use as the attribute name.
	 * @param attributeName
	 * @param menuOption
	 * @return
	 */
	public CommonSolvent selectDropDownMenuByAttributeName(String attributeName, String menuOption) {
		String menuLocator = "//select[contains(@name, '" + attributeName + "')]";
		selenium.waitForElement(menuLocator);
		selenium.select(menuLocator, menuOption);
		return this;
	}
	
	/**
	 * Method selects an option from a drop down menu based on the given option. 
	 * <b>example: </b> <i> idName="newMemberId", menuOption="QA_Test10" </i>
	 * @param idName selection Id or name
	 * @param menuOption Option in the drop down list
	 * @return
	 */
	public CommonSolvent selectDropDownMenuByIdName(String idName, String menuOption) {
		String menuLocator = "//select[@id='" + idName + "']";
		String menuLocator1 = "//select[@name='" + idName + "']";
		if(selenium.isElementPresent(menuLocator)) {
			selenium.select(menuLocator, menuOption);
		}
		else if (selenium.isElementPresent(menuLocator1)) {
			selenium.select(menuLocator1, menuOption);
		}	
		if(!selenium.isElementPresent(menuLocator) && !selenium.isElementPresent(menuLocator1))
			throw new SolventSeleniumException("Drop down menu with " + menuLocator + "or" + menuLocator1 +  "is not available");
		return this;
	}
	
	/**
	 * Some delete action trigger windows confirmation window this method should 
	 * be used to click ok button on the confirmation window.
	 * @return
	 */
	public CommonSolvent clickOkOnConfirmation() {
		selenium.waitForConfirmation();
		selenium.chooseOkOnNextConfirmation();
		return this;
	}

	public void clickOkOnConfirmation(String confirmationLocator) {
	    selenium.waitForElement(confirmationLocator);
	    selenium.click(confirmationLocator+"//*[normalize-space(text())='OK']");
	    selenium.waitForPageToLoad();
	}
	
	/**
	 * Some delete action trigger windows confirmation window, this method should 
	 * be used to click cancel button on the confirmation window.
	 * @return
	 */	
	public CommonSolvent clickCancelOnConfirmation() {
		selenium.waitForConfirmation();
		selenium.chooseCancelOnNextConfirmation();
		return this;
	}
	
	/**
	 * Method to get base url of the application.
	 * Should only be used to test direct urls.
	 * @return
	 */
	public String getBaseURL() {
		return selenium.getBaseURL();
	}
	
	/**
	 * Based on the label in-front of the magnifying glass method identifies the magnifying 
	 * glass icon and make a click action.
	 * @param magnifyLable label in-front of the magnifying glass.
	 * @return
	 */
	public CommonSolvent clickMagnifyingIcon(String magnifyLable) {
		String locator = "//img/preceding::div[normalize-space(text())='" + magnifyLable 
						+ "']/following::img[@class='cssicon'][1]";
		selenium.waitForElement(locator);
		selenium.click(locator);
		selenium.waitForPageToLoad();
		return this;
	}
	
	/**
	 * Method returns the text of the link if the link with a given name was found.
	 * @param linkName name of the link.
	 * @return
	 */
	public String getLinkText(String linkName) {
		String locator = "//a[normalize-space(text())='" + linkName + "']";

		selenium.waitForElement(locator);
		String linkText = selenium.getText(locator);
		return linkText;
	}
	
	/**
	 * Method returns the text inside a text field if the label of the given text field found.
	 * @param txtFieldLabel name of the label of the text field.
	 * @return
	 */
	public String getTextFieldValueByLabel(String txtFieldLabel) {
		String txtFieldLocator = "//input//preceding::td[1][normalize-space(text())='" + txtFieldLabel + "']//following::input[1]";
		String txtFieldTxt = null;
		if(isTextPresent(txtFieldLabel)) {
			if(!selenium.isElementPresent(txtFieldLocator))
				throw new SolventSeleniumException("Unable to find the txt field with label " + txtFieldLabel + " at " + txtFieldLocator);
			txtFieldTxt = selenium.getValue(txtFieldLocator);
		} 
		return txtFieldTxt;
	}
	
	/**
	 * Return a boolean value if the link exist on a web page.
	 * @param linkName text value of the link that is searching.
	 * @return
	 */
	public boolean isLinkPresent(String linkName) {
	    return isLinkPresent(linkName, Duration.standardSeconds(5));
	}

	public boolean isLinkPresent(String linkName, Duration wait) {
        Locator locator = new CssLocator("a:contains('"+ linkName +"')");
        String linkText = null;
        
        try {
            selenium.waitForElement(locator, wait);
            linkText = selenium.getText(locator);
        }catch (SeleniumException e) {
            e.printStackTrace();
        }
        
        if (linkText != null)
            return true;
        else
            return false;
    }

	
	/**
	 * Attached a file based when given a input filed and location of the file.
	 * Location of the file needs to be a webserver Ex:http://some.com/file.csv.
	 * @param fieldLocator location of the input field.
	 * @param fileLocator location of the file location (webserver).
	 * @return
	 */
	public CommonSolvent attachFile(String fieldLocator, String fileLocator) {
		String inputField = "//input//following::div[(text()='" + fieldLocator + "')]/following::input[1]";
		selenium.attachFile(inputField, fileLocator);
		return this;
	}
	
	/**
	 * Answer on windows prompt. <br>
	 * This method can be used to answer windows prompts. 
	 * Example: if the window has OK,Cancel
	 * @return
	 */
	public CommonSolvent answerWindowsPrompt(String answer) {
		selenium.answerOnNextPrompt(answer);
		return this;
	}
	
	/**
	 * Method select multiple items from a list box.<br>
	 * <b>usage:</b>selectFromMultiListBox("Select Attribute:", "Blink Count", "Demand");
	 * @param listBoxName name of the list box.
	 * @param options multiple options inside the list box. 
	 * @return
	 */
	public CommonSolvent selectFromMultiListBox(String listBoxName, String... options) {
		String listBoxLocator = "//*[normalize-space(text())='" + listBoxName + "']//following::select[1]";
		selenium.waitForElement(listBoxLocator);
		if(!selenium.isElementPresent(listBoxLocator))
			throw new SolventSeleniumException("Unable to find multi-list box with label " + listBoxLocator);
		for(String option : options) {
			selenium.addSelection(listBoxLocator, option);
		}
		return this;
	}
	
	/**
	 * Method click a radio button based on the label name of the radio button.
	 * @param radioButtonName name of the radio button.
	 * @return
	 */
	public CommonSolvent clickRadioButtonByName(String radioButtonName) {
		String buttonLocator = "//input//preceding::*[normalize-space(text())='" + radioButtonName + "']/preceding::input[1]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find radio button with label " + radioButtonName + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Given a string with spaces, the method will split each word(string) by space and return as an array of strings.
	 * @param label string value of the label that needs to be split by space.
	 * @return afterSplit An Array of Strings.
	 */
	public String[] getLabelValuesAfterSplit(String label) {
		String[] afterSplit = null;
		if(!label.equals(null) || !label.equals(" ") || !label.equals(""))
			afterSplit = label.split("[ ]");
		for(int i = 0; i < afterSplit.length; i++) {
			String removedChar = "";
			String temp = afterSplit[i];
			for(int j = 0; j < temp.length(); j++) {
				if(temp.charAt(j) != ',')
					removedChar += temp.charAt(j);				
			}
			if(removedChar == "")
				afterSplit[i] = temp;
			else
				afterSplit[i] = removedChar;
		}
		return afterSplit;
	}
	
	/**
	 * Method return the current date in the format of mm/dd/yyyy.
	 * @return
	 */
	public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy");
        return simpleFormat.format(calendar.getTime());   		
	}
	
	/**
	 * Method return the current time in the format of hh:mm:ss.
	 * @return
	 */
	public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleFormat =  new SimpleDateFormat("hh:mm:ss");
        return simpleFormat.format(calendar.getTime());   	
	}

	/**
	 * Get Current date/time based on custom format.
	 * @param dateFormat custom format of the date/time.
	 * @return
	 */
	 public String getCurrentTime(String dateFormat) {
		 Calendar calendar = Calendar.getInstance();
		 SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		 return sdf.format(calendar.getTime());
	 }
	 
	 
	 /**
	  * Method enters text in Primary Operator Login and Secondary Operator Login pages, this is a temporary method.
	  * <b>example: </b> <i> id="adminUsername" </i> id is attributeType, adminUsername is attribute.
	  * @param attributeType 
	  * @param attribute
	  * @param text
	  * @return
	  */
	 public CommonSolvent enterTextByAttribute(String attributeType, String attribute, String text) {
			String txtLocator = "//*[@" + attributeType + "='" + attribute + "']";
			selenium.waitForElement(txtLocator, 3000);
			if(!selenium.isElementPresent(txtLocator))
				throw new SolventSeleniumException("Unable to find " + attributeType + " type " + attribute);
			selenium.type(txtLocator, text);
			return this;
		}
	 
	 /**
	 * Enters specified text in edit box with the given field label name.
	 * 
	 * @deprecated use enterInputText instead.
	 * 
	 * @param inputField name of the field that text are to be typed in.
	 * @param text the text to be entered.
	 * @return 
	 */
	 @Deprecated
	 public CommonSolvent enterTextByLabel(String inputField, String text) {
		String labelLocator = "//td/label[normalize-space(text())='" + inputField + "']//following::input[1]";
//		Locator labelLocator = new CssLocator("form tr:contains('" + inputField + "') > td > label input");

	    selenium.waitForElement(labelLocator);
	    selenium.type(labelLocator, text);
	    return this;
	 }
	 
	/**
	* Method click a radio button based on value of the radio button.
	* Radio buttons on Thermostat page. 
	* @param radioButtonValue value of the radio button.
	* For example, value = fahrenheit,ALL,WEEKDAY_SAT_SUN
	* @return
	*/
	 public CommonSolvent clickRadioButtonByLabelValue(String radioButtonValue) {
		String buttonLocator = "//input[@value='" + radioButtonValue + "' and @type='radio']";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Unable to find radio button with " + radioButtonValue + " at " + buttonLocator);
		selenium.click(buttonLocator);
		return this;
	}


///// New Usefully Methods.  These should be used when possible.
	public void enterInputText(String formAction, String inputFieldName, int elementNumber, String text, Duration wait) {

	    // TODO figure out a way to grab the nth elment.
	    String formLocator = "//form[contains(@action, '"+formAction+"')]";
	    String inputLocator = formLocator + "//input[@name='"+inputFieldName+"']["+elementNumber+"]";
//        String formActionPath = "css=form[action*="+formAction+"] ";
//	    String inputLocator = formActionPath + "input[name="+inputFieldName+"]:nth-of-type("+elementNumber+")";
	    
	    selenium.waitForElement(inputLocator, wait);
	    selenium.type(inputLocator, text);
	}
	
	public void enterInputTextByFormId(String formId, String inputFieldName, String text) {
        
	    String escapedFormId = LocatorEscapeUtils.escapeCss(formId);
	    Locator formLocator = new CssLocator("form#"+escapedFormId+"");
	    
	    String escapedInputFieldName = LocatorEscapeUtils.escapeCss(inputFieldName);
        Locator inputLocator = formLocator.append("input[name='"+escapedInputFieldName+"']");
        
        selenium.waitForElement(inputLocator);
        selenium.type(inputLocator, text);
    }
	
	public void enterInputText(String formAction, String inputFieldName, int elementNumber, String text) {
	    enterInputText(formAction, inputFieldName, elementNumber, text, Duration.standardSeconds(3));
	}   

	public void enterInputText(String formAction, String inputFieldName, String text, Duration wait) {
        Locator formActionLocator = getFormActionCssLocator(formAction);
        String escapedInputFieldName = LocatorEscapeUtils.escapeCss(inputFieldName);
        Locator inputLocator = formActionLocator.append("input[name='"+escapedInputFieldName+"']");
        Locator textAreaLocator = formActionLocator.append("textarea[name='"+escapedInputFieldName+"']");
        
        if (selenium.isElementPresent(inputLocator, wait)) {
            selenium.type(inputLocator, text);
            return;
        }
        
        if (selenium.isElementPresent(textAreaLocator, wait)) {
            selenium.type(textAreaLocator, text);
            return;
        }
        
        throw new IllegalStateException("The page element "+inputFieldName+" cound not be found in the form "+formAction+".");
	}   
	
	public void enterInputText(String formAction, String inputFieldName, String text) {
	    enterInputText(formAction, inputFieldName, text, Duration.standardSeconds(3));
	}
	
	public boolean isEnterInputTextPresent(String formAction, String inputFieldName) {
        Locator formActionLocator = getFormActionCssLocator(formAction);
        String escapedInputFieldName = LocatorEscapeUtils.escapeCss(inputFieldName);
        Locator inputLocator = formActionLocator.append("input[name='"+escapedInputFieldName+"']");
        
        return selenium.isElementPresent(inputLocator, Duration.ZERO);
    }


	public void selectDropDownMenu(String formAction, String selectName, String menuOption) {
	    Locator formActionLocator = getFormActionCssLocator(formAction);
	    Locator menuLocator = formActionLocator.append("select[name='"+selectName+"']");
	    
	    selenium.waitForElement(menuLocator);
	    selenium.select(menuLocator, menuOption);
	}

	public void selectDropDownMenuWithPageLoad(String formAction, String selectName, String menuOption) {
	    selectDropDownMenu(formAction, selectName, menuOption);
	    selenium.waitForPageToLoad();
	}

	public boolean isSelectDropDownPresent(String formAction, String selectName) {
        Locator formActionLocator = getFormActionCssLocator(formAction);
        Locator dropDownLocator = formActionLocator.append("select[name='"+selectName+"']");
	    
        return selenium.isElementPresent(dropDownLocator, Duration.ZERO);
	}
	
    /**
     * This method should be used when clicking a button inside a form
     * @param formAction - This is the action attribute of the html form tag.
     *                      In a lot of cases this can be shortened to just the 
     *                      last part of the URL.  EX. "updateAccount"
     * @param buttonName - This is the name attribute of the html input field  
     */
    public void clickFormButton(String formAction, String buttonName) {

        Locator formActionLocator = getFormActionCssLocator(formAction);
        Locator inputLocator = formActionLocator.append("input[name="+buttonName+"]");
        Locator buttonLocator = formActionLocator.append("button[name="+buttonName+"]");

        // Check and see if it is an input button.
        if (selenium.isElementPresent(inputLocator, Duration.ZERO)) {
            selenium.click(inputLocator);
            selenium.waitForPageToLoad();
            return;
            
        // Check and see if it is an submit button.
        } else if (selenium.isElementPresent(buttonLocator, Duration.ZERO)) {
            selenium.click(buttonLocator);
            selenium.waitForPageToLoad();
            return;
        }
        
        throw new IllegalStateException("The button "+buttonName+" does not exist");
    }
    
    /**
     * This method should be used when clicking a button that is linked to another page
     * 
     * IMPORTANT NOTE: Software is looking to fixing this issue.  We should never be
     * using javascript to replicate the action of a link.  This should be done through an anchor tag.
     * 
     * @param formAction - This is the onclick attribute of the button you wish to click.
     *                      In a lot of cases this can be shortened to just the 
     *                      last part of the URL.  EX. "updateAccount"
     * @param buttonName - This is the name attribute of the html button field. If null the button syntax
     *                     will be ignored.
     */
    public void clickLinkButton(String formAction, String buttonName) {

        // IMPORTANT NOTE: Software is looking to fixing this issue.  We should never be
        // using javascript to replicate the action of a link.  This should be done through an anchor tag.
        String escapedFormAction = LocatorEscapeUtils.escapeCss(formAction);
        Locator javaScriptButtonLocator = new CssLocator("button[data-href*='"+escapedFormAction+"']");
        if (buttonName != null) {
            javaScriptButtonLocator.append("[name='"+buttonName+"']");
        }
            
        // Check and see if it is an submit button.
        if (selenium.isElementPresent(javaScriptButtonLocator, Duration.ZERO)) {
            selenium.click(javaScriptButtonLocator);
            selenium.waitForPageToLoad();
            return;
        }
        
        throw new IllegalStateException("The button "+buttonName+" does not exist");
    }

    /**
     * This method should be used when clicking a button inside a form
     * @param formAction - This is the action attribute of the html form tag.
     *                      In a lot of cases this can be shortened to just the 
     *                      last part of the URL.  EX. "updateAccount"
     * @param buttonName - This is the name attribute of the html input field  
     */
    public void clickFormButtonByButtonId(String formAction, String buttonId) {
        
        Locator formActionLocator = getFormActionCssLocator(formAction);
        Locator buttonLocator = formActionLocator.append("button#"+buttonId);
        
        selenium.click(buttonLocator);
        selenium.waitForPageToLoad();
    }
    

    /**
     * This method is meant to get us by for the time being.  This should
     * be replaced by the method above or a method that allows us to uniquely
     * identify a button inside a form.
     *
     * @deprecated This method should not be used in the future, once
     *    software comes up with a unique way to identify buttons.
     *
     * @param formAction - This is the action attribute of the html form tag.
     *                      In a lot of cases this can be shortened to just the 
     *                      last part of the URL.  EX. "updateAccount"
     * @param buttonValue - This is the name attribute of the html input field  
     */
    @Deprecated
    public void clickFormButtonByButtonValue(String formAction, String buttonValue) {

        Locator formActionLocator = getFormActionCssLocator(formAction);
        Locator buttonLocator = formActionLocator.append("input[value="+buttonValue+"]");
        
        selenium.click(buttonLocator);
        selenium.waitForPageToLoad();
    }
	 
    /**
     * In most cases you should use clickFormButton instead of this method.
     * 
     * @deprecated This method should not be used in the future, once
     *    software comes up with a unique way to identify buttons.
     *
     * 
	 * @param formName - This is the name attribute of the html form tag
	 * @param buttonName - This is the name attribute of the html input field  
	 */
    @Deprecated
    public void clickFormButtonByFormName(String formName, String buttonName) {

        String formPath = "//form[@name='"+formName+"']";
        String buttonPath = formPath+"//input[@name='"+buttonName+"']";
        
        selenium.click(buttonPath);
        selenium.waitForPageToLoad();
    }

    // Helper Methods
    public CssLocator getFormActionCssLocator(String formAction) {
        String escapedFormAction = LocatorEscapeUtils.escapeCss(formAction);
        return new CssLocator("form[action*='"+escapedFormAction+"']");
    }

    // NEW ASSERT METHODS //
    /**
     * 
     */
    public void assertEqualsTextIsPresent(String text, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            boolean textIsPresent = isTextPresent(text, Duration.ZERO);
            if (textIsPresent) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The text was not found before the page timed out.");
    }
    
   /**
     * 
     */
    public void assertEqualsTextIsPresent(String text) {
        assertEqualsTextIsPresent(text, Duration.standardSeconds(20));
    }

    /**
     * 
     */
    public void assertEqualsTextIsNotPresent(String text, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            boolean textIsPresent = !isTextPresent(text, Duration.ZERO);
            if (textIsPresent) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The text was not found before the page timed out.");

    }
    
   /**
     * 
     */
    public void assertEqualsTextIsNotPresent(String text) {
        assertEqualsTextIsNotPresent(text, Duration.standardSeconds(20));
    }

    /**
     * 
     */
    public void assertEqualsLinkTextNotPresent(String text, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            boolean isLinkTextPresent = !isLinkPresent(text, Duration.ZERO);
            if (isLinkTextPresent) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The text was not found before the page timed out.");
    }

    /**
     * 
     */
    public void assertEqualsLinkTextNotPresent(String text) {
        assertEqualsLinkTextNotPresent(text, Duration.standardSeconds(5));
    }

}
