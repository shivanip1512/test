/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import org.joda.time.Duration;
import org.junit.Assert;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.metering.MeteringSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Solvent that handles all the widgets in Yukon application.
 * <br><br>
 * @author anuradha.uduwage
 *
 */
public class WidgetSolvent extends AbstractSolvent {

	/**
	 * @param params
	 */
	public WidgetSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
	
	/**
	 * Method can be used to minimize and expand the widgets on any page.
	 * @param widgetName Title/Name of the Widget
	 * @return
	 */
	public WidgetSolvent expandCollapseWidgetByTitle(String widgetName) {
		String plusMinusLocator = getXpathBaseForWidgetMinMax(widgetName);
		if(!selenium.isElementPresent(plusMinusLocator))
			throw new SeleniumException("Unable to find widget " + widgetName + "did you spell it correct?");
		selenium.click(plusMinusLocator);
		return this;
	}
	
	/**
	 * Construct the baseXpath for plus minus icon and return its XPath as a string.
	 * @param widgetTitle title of the widget.
	 * @return XPath as a string.
	 */
	private String getXpathBaseForWidgetMinMax(String widgetTitle) {
		String baseLocator = getXpathRootForWidgetTitle(widgetTitle);
		String minusLocator = baseLocator + "/preceding::img[@class='minMax' and contains(@id, 'minusImg')][1]";
		String plusLocator = baseLocator + "/preceding::img[@class='minMax' and contains(@id, 'plusImg')][1]";
		String plusMinusXpath = "";
		if(selenium.isElementPresentAndVisible(minusLocator, 10000))
			plusMinusXpath = minusLocator;
		if(selenium.isElementPresentAndVisible(plusLocator, 10000))
			plusMinusXpath = plusLocator;
		
		return plusMinusXpath;
	}
	
	/**
	 * Returns the title of the widget.
	 * @param widgetTitle name of the widget
	 * @return
	 */
	public String getWidgetTitle(String widgetTitle) {
		selenium.waitForElement(getXpathRootForWidgetTitle(widgetTitle));
		String title = selenium.getText(getXpathRootForWidgetTitle(widgetTitle));
		return title;
	}
	
	/**
	 * Click a button that is inside a widget.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonValue Value of the button.
	 * @return
	 */
	public WidgetSolvent clickButtonByWidget(String widgetTitle, String buttonValue) {
		String buttonLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::button[@value='" + buttonValue + "'][1]";
		String buttonLocator1 = getXpathRootForWidgetTitle(widgetTitle) + "/following::input[contains(@value, '" + buttonValue + "')][1]";
		String widgetName = getXpathRootForWidgetTitle(widgetTitle);
		selenium.waitForAjax();
		selenium.waitForElement(widgetName, 5000);
		if(selenium.isElementPresent(buttonLocator)) {
			selenium.click(buttonLocator);
		}
		else if(selenium.isElementPresent(buttonLocator1)) {
			selenium.click(buttonLocator1);
		}
		else {
			if(!selenium.isElementPresent(buttonLocator, 5000) && !selenium.isElementPresent(buttonLocator1)) 
				throw new SolventSeleniumException("Button " + buttonValue + " is not available in widget " + widgetTitle);
		}
		return this;
	}
	
	/**
	 * Click a button that is inside a widget by its id.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonName name of the button.
	 * @return
	 */
	public WidgetSolvent clickButtonInWidgetByID(String widgetTitle, String buttonID) {
		System.out.println(getXpathRootForWidgetTitle(widgetTitle));
		String buttonLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::button[contains(@id, '" + buttonID + "')]";
		String widgetName = getXpathRootForWidgetTitle(widgetTitle);
		selenium.waitForAjax();
		selenium.waitForElement(widgetName, 5000);
//		if(!selenium.isElementPresentAndVisible(widgetName, 5000))
//			throw new SolventSeleniumException("Widget with the name " + widgetTitle + " is not available");
		selenium.waitForElementToBecomeVisible(buttonLocator, 5000);
//		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
//			throw new SolventSeleniumException("Button " + buttonID + " is not available in widget " + widgetTitle);
		selenium.waitForAjax();
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Click a button that is inside a widget by its id.
	 * @param widgetTitle name/title of the widget.
	 * @param buttonType Type of the button.
	 * @return
	 */
	public WidgetSolvent clickButtonInWidgetByType(String widgetTitle, String buttonType) {
		System.out.println(getXpathRootForWidgetTitle(widgetTitle));
		String buttonLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::button[contains(@type, '" + buttonType + "')]";
		String widgetName = getXpathRootForWidgetTitle(widgetTitle);
		selenium.waitForAjax();
		selenium.waitForElement(widgetName, 5000);
//		if(!selenium.isElementPresentAndVisible(widgetName, 5000))
//			throw new SolventSeleniumException("Widget with the name " + widgetTitle + " is not available");
		selenium.waitForElementToBecomeVisible(buttonLocator, 5000);
//		if(!selenium.isElementPresentAndVisible(buttonLocator, 5000))
//			throw new SolventSeleniumException("Button " + buttonType + " is not available in widget " + widgetTitle);
		selenium.waitForAjax();
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Click a link that is inside of widget.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public WidgetSolvent clickLinkByWidget(String widgetTitle, String linkName) {
		String linkLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::a[normalize-space(.)='" + linkName + "'][1]";
		String widgetName = getXpathRootForWidgetTitle(widgetTitle);
		selenium.waitForElement(widgetName);
//		if(!selenium.isElementPresent(widgetName, 5000)) {
//			throw new SolventSeleniumException("Widget with the name " + widgetTitle + " is not available");
//		}
		selenium.waitForElement(linkLocator, 5000);
//		if(!selenium.isElementPresent(linkLocator, 5000)) {
//		    throw new SolventSeleniumException("Button " + linkName + " is not available in widget " + widgetTitle);
//		}

		selenium.click(linkLocator);
		return this;
	}

   /**
     * This method is an extension of the clickLinkByWidget which uses a waitForPageToLoad when it's done.
     */
    public WidgetSolvent clickLinkByWidgetWithPageLoadWait(String widgetTitle, String linkName) {
        clickLinkByWidget(widgetTitle, linkName);
        selenium.waitForPageToLoad();
        return this;
    }
	
	/**
	 * Click enable button to enable a monitor based on widget name and monitor name.
	 * @param widgetTitle name of the widget.
	 * @param monitorName name of the monitor.
	 * @return
	 */
	public MeteringSolvent enableByWidgetAndMonitorName(String widgetTitle, String monitorName) {
		String enableLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::a[normalize-space(.)='" 
			+ monitorName + "'][1]/following::button[contains(@title, 'Enable (" + monitorName + "')]";
		selenium.waitForElement(enableLocator);
		if(!selenium.isElementPresent(enableLocator))
			throw new SolventSeleniumException("Enable button is not available for monitor with a name " + monitorName);
		selenium.click(enableLocator);
		selenium.waitForAjax();
		return new MeteringSolvent();
	}
	
	/**
	 * Click disable button to disable a monitor based on widget name and monitor name.
	 * @param widgetTitle name of the widget.
	 * @param monitorName name of the monitor.
	 * @return
	 */
	public MeteringSolvent disableByWidgetAndMonitorName(String widgetTitle, String monitorName) {
		String disableLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::a[normalize-space(.)='" 
			+ monitorName + "'][1]/following::button[contains(@title, 'Disable (" + monitorName + ")')]";
		selenium.waitForElement(disableLocator);
		if(!selenium.isElementPresent(disableLocator))
			throw new SolventSeleniumException("Disable button is not available for monitor with a name " + monitorName);
		selenium.click(disableLocator);
		selenium.waitForAjax();
		return new MeteringSolvent();
	}
	
	/**
	 * Click Manage processor image link to navigate to monitor processing page.
	 * @param widgetTitle name of the widget.
	 * @param monitorName name of the monitor.
	 * @return
	 */
	public MeteringSolvent viewMonitorDetailsByName(String widgetTitle, String monitorName) {
		String iconLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::a[normalize-space(.)='" 
			+ monitorName + "']/preceding::a[contains(@title, '(" + monitorName + ")')]";
		selenium.waitForElement(iconLocator);
		if(!selenium.isElementPresent(iconLocator))
			throw new SolventSeleniumException("Manage processor icon for " + monitorName + " is not available at " + iconLocator);
		selenium.click(iconLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Click view schedule details by schedule name, and widget.
	 * @param widgetTitle name of the widget that schedule is in.
	 * @param scheduleName name of the schedule.
	 * @return
	 */
	public MeteringSolvent viewScheduleDetailsByName(String widgetTitle, String scheduleName) {
		String scheduleLocator = getXpathRootForWidgetTitle(widgetTitle) + "/following::a[normalize-space(.)='" 
			+ scheduleName + "']/preceding::a[contains(@title, '(" + scheduleName + ")')]";
		selenium.waitForElement(scheduleLocator);
		if(!selenium.isElementPresent(scheduleLocator))
			throw new SolventSeleniumException("View Schedule details icon for " + scheduleName + " is not available at " + scheduleLocator);
		selenium.click(scheduleLocator);
		return new MeteringSolvent();
	}
	
	/**
	 * Method return text from a widget based on the label inside a widget.
	 * @param widgetTitle name of the widget.
	 * @param lableName name of the lable of the widget.
	 * @return
	 */
	public String getTextFromWidgetByLabel(String widgetTitle, String labelName) {
		String getText = null;
		String widgetTxtLocatorBase = "//div[@class='title boxContainer_title' and contains(text(), '"+widgetTitle+"')]"+
		                              "/following::td[normalize-space(text())='" + labelName + "']";
		
		if(labelName.equalsIgnoreCase("Previous Usage Reading:")) {
			String widgetTxtLocator = widgetTxtLocatorBase + "/following::select//option[2]";
			selenium.waitForElement(widgetTxtLocator);
			getText = selenium.getText(widgetTxtLocator);
		} else {
			String widgetTxtLocator = widgetTxtLocatorBase +"/following::td[1]";
			selenium.waitForElement(widgetTxtLocator);
			getText = selenium.getText(widgetTxtLocator);
		}
		
		return getText;
	}
	
	/**
	 * Method return the name of link based on a widget header and the name of the link.
	 * @param widgetTitle Title of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public String getLinkFromWidgetByLinkName(String widgetTitle, String linkName) {
		String getLinkText = null;
		String widgetLinkLocator = getXpathRootNonMixMaxWidget(widgetTitle) + "/following::a[normalize-space(text())='" + linkName + "'][1]";
		selenium.waitForElement(widgetLinkLocator);
		getLinkText = selenium.getText(widgetLinkLocator);
		return getLinkText;
	}
	
	/**
	 * Construct Xpath root for the title of the widget.
	 * @param widgetTitle title of the wideget.
	 * @return root xpath for the title of the widget.
	 */
	protected String getXpathRootForWidgetTitle(String widgetTitle) {
		String root = "//div[@class='title boxContainer_title' and contains(text(),'" + widgetTitle + "')]";
		return root;
	}
	
	/**
	 * Construct the xpath root for the title of widget that doesn't have mix max button.
	 * @param widgetTitle name of the widget.
	 * @return
	 */
	protected String getXpathRootNonMixMaxWidget(String widgetTitle) {
		String root = "//div[@class='title boxContainer_title' and contains(., '" + widgetTitle +"')]";
		return root;
	}
	
	
    // NEW ASSERT METHODS //
    /**
     * 
     */
    public void assertEqualsTextFromWidgetByLabel(String desiredValue, String widgetTitle, String labelName, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            String demandResponseState = getTextFromWidgetByLabel(widgetTitle, labelName);
            if (demandResponseState.equals(desiredValue)) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The widget label did not change to the expected value before the timeout.");

    }
    
   /**
     * 
     */
    public void assertEqualsTextFromWidgetByLabel(String desiredValue, String widgetTitle, String labelName) {
        assertEqualsTextFromWidgetByLabel(desiredValue, widgetTitle, labelName, Duration.standardSeconds(20));
    }

}
