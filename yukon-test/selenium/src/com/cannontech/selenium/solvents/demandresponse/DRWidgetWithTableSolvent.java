/**
 * 
 */
package com.cannontech.selenium.solvents.demandresponse;

import org.joda.time.Duration;
import org.junit.Assert;

import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class is defined to handle functions when data are inside a table but the table
 * is wrapped around with a widget.
 * <b>Example:</b> DR page has Favorites widget and Recently Viewed Items widget.
 * 
 * @author anuradha.uduwage
 *
 */
public class DRWidgetWithTableSolvent extends CommonTableSolvent {

	/**
	 * Constructor of the class that sets the table class.
	 * @param params
	 */
	public DRWidgetWithTableSolvent(String... params) {
		super("tableClass=compactResultsTable rowHighlighting");
	}
	
	/**
	 * Method will click Start action button based on Widget title and the link name.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public DRWidgetWithTableSolvent clickStartByWidgetAndLinkName(String widgetTitle, String linkName) {
		String startButtonLocator = widgetXpathRoot(widgetTitle) + "/following::td/a[normalize-space(text())='" + linkName + "']/following::img[contains(@title, 'Start')][1]";
		if(isWidgetPresent(widgetTitle)) {
			selenium.waitForElement(startButtonLocator);
			if(!selenium.isElementPresent(startButtonLocator))
				throw new SolventSeleniumException("Start button for " + linkName + " is not available at " + startButtonLocator);
			selenium.click(startButtonLocator);
		} else {
			throw new SolventSeleniumException("Unable to find widget with the name " + widgetTitle + " at " + widgetXpathRoot(widgetTitle));
		}
		return this;
	}
	
	/**
	 * Method will click Stop action button based on widget title and the link name.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public DRWidgetWithTableSolvent clickStopByWidgetAndLinkName(String widgetTitle, String linkName) {
		String stopButtonLocator = widgetXpathRoot(widgetTitle) + "/following::td/a[normalize-space(text())='" + linkName + "']/following::img[contains(@title, 'Stop')][1]";
		if(isWidgetPresent(widgetTitle)) {
			selenium.waitForElement(stopButtonLocator);
			if(!selenium.isElementPresent(stopButtonLocator))
				throw new SolventSeleniumException("Stop button for " + linkName + " is not available at " + stopButtonLocator);
			selenium.click(stopButtonLocator);
		}
		else 
			throw new SolventSeleniumException("Unable to find widget with the name " + widgetTitle + " at " + widgetXpathRoot(widgetTitle));
		return this;		
	}
	
	/**
	 * Method return the string value of the type based on the widget name and the link name.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public String getTypeByWidgetAndLinkName(String widgetTitle, String linkName) {
		String typeLocator = widgetXpathRoot(widgetTitle) + "/following::td/a[normalize-space(text())='" + linkName + "']/following::td[1]";
		String getType = null;
		if(isWidgetPresent(widgetTitle)) {
			selenium.waitForElement(typeLocator);
			if(!selenium.isElementPresent(typeLocator))
				throw new SolventSeleniumException("Unable to find Type under " + widgetTitle + " for link " + linkName + " at " + typeLocator);
			getType = selenium.getText(typeLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find widget with the name " + widgetTitle + " at " + widgetXpathRoot(widgetTitle));
		return getType.trim();
	}
	
	/**
	 * Method return the string value of the state based on the widget name and the link name.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public String getStateByWidgetAndLinkName(String widgetTitle, String linkName) {
		String stateLocator = widgetXpathRoot(widgetTitle) + "/following::td/a[normalize-space(text())='" + linkName + "']/following::td[2]";
		String getState = null;
		if(isWidgetPresent(widgetTitle)) {
			selenium.waitForElement(stateLocator);
			if(!selenium.isElementPresent(stateLocator))
				throw new SolventSeleniumException("Unable to find State under " + widgetTitle + " for link " + linkName + " at " + stateLocator);
			getState = selenium.getText(stateLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find widget with the name " + widgetTitle + " at " + widgetXpathRoot(widgetTitle));
		return getState.trim();		
	}
	
	/**
	 * Method click the favorite icon based on widget title and name of the link.
	 * @param widgetTitle name of the widget.
	 * @param linkName name of the link.
	 * @return
	 */
	public DRWidgetWithTableSolvent addOrRemoveFromFavoritesByName(String widgetTitle, String linkName) {
		String favoriteIconLocator = widgetXpathRoot(widgetTitle) + "/following::a[normalize-space(text())='" + linkName + "'][1]/preceding::a[1]";
		if(isWidgetPresent(widgetTitle)) {
			if(!selenium.isElementPresent(favoriteIconLocator))
				throw new SolventSeleniumException("Unable to find favorite icon for link " + linkName + " at " + favoriteIconLocator);
			selenium.click(favoriteIconLocator);
		}
		else
			throw new SolventSeleniumException("Unable to find widget with the name " + widgetTitle + " at " + widgetXpathRoot(widgetTitle));
		return this;
	}
	
	/**
	 * Set the xpath for the widget including the table.
	 * @param widgetTitle name of the widget.
	 * @return
	 */
	private String widgetXpathRoot(String widgetTitle) {
		return getXpathRoot() + "/preceding::div[@class='title boxContainer_title' and contains(., '" + widgetTitle + "')]";
	}
	
	/**
	 * Helper method to do a sanity check on the existence of widget. 
	 * @param widgetTitle name of the widget.
	 * @return
	 */
	private boolean isWidgetPresent(String widgetTitle) {
		boolean isWidgetPresent;
		String widgetTitleLocator = widgetXpathRoot(widgetTitle);
		if(!selenium.isElementPresent(widgetTitleLocator))
			isWidgetPresent = false;
		else
			isWidgetPresent = true;
		return isWidgetPresent;
	}

    // NEW ASSERT METHODS //
    /**
     * 
     */
    public void assertEqualsStateByWidgetAndLinkName(String desiredValue, String widgetTitle, String linkName, Duration wait) {
        while(!wait.isShorterThan(Duration.ZERO)) {
            String demandResponseState = getStateByWidgetAndLinkName(widgetTitle, linkName);
            if (demandResponseState.equals(desiredValue)) {
                return;
            } else {
                wait = delayForRecheck(wait);
            }
        }

        Assert.fail("The state did not change to the expected value before the timeout.");

    }
    
   /**
     * 
     */
    public void assertEqualsStateByWidgetAndLinkName(String desiredValue, String widgetTitle, String linkName) {
        assertEqualsStateByWidgetAndLinkName(desiredValue, widgetTitle, linkName, Duration.standardSeconds(20));
    }
	
}
