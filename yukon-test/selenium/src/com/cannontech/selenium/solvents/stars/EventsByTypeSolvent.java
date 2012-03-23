/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.thoughtworks.selenium.SeleniumException;

/**
 * This Solvent method handle start even by type section and provide solvents methods 
 * that are specific to Events section of the application.
 * 
 * @author anuradha.uduwage
 *
 */
public class EventsByTypeSolvent extends AbstractSolvent {
	
	//This variable sets the Event Log initial date X days from the current
	final static int initialDate = -32;
	//These variables set the start and stop opt out dates X days from the current
	final int optOutStartDate1 = -21;
	final int optOutStartDate2 = -7;
	final int optOutStopDate1 = -21;
	final int optOutStopDate2 = -7;

	/* (non-Javadoc)
	 * @see com.cannontech.selenium.core.AbstractSolvent#prepare()
	 */
	@Override
	public void prepare() {
		//nothing at the moment.
	}
	

	/**
     * Method returns the given date offset by fromCurrent
     * @param fromCurrent
     * @return
     */
    public String returnDate(int fromCurrent){
    	   Calendar cal = Calendar.getInstance();
    	   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    	   cal.add(Calendar.DATE, fromCurrent);
    	   return dateFormat.format(cal.getTime());
    }
    
    /**
     * Method returns true if date is within the start and stop dates. All dates
     * must be given as a String with the format MM/dd/yyyy.
     * @param date
     * @param start
     * @param stop
     * @return
     */
    public boolean dateWithinRange(String date, String start, String stop){
    	boolean withinRange = false;
    	try{
	    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	    	Date compare = df.parse(date);
	    	Date begin = df.parse(start);
	    	Date end = df.parse(stop);
	    	withinRange = !(compare.before(begin) || compare.after(end));
    	} catch(Exception e){ e.printStackTrace(); }
    	return withinRange;
    }
	
	/**
	 * Method fills in two dates after the given label name
	 * @param attribute
	 * @param value
	 * @return
	 */
	public EventsByTypeSolvent fillInTwoDates(String label, String Date1, String Date2) {
		String date1Locator = "//td[contains(., '"+label+"')]/following::input";
		String date2Locator = date1Locator+"/following::input";
		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(date1Locator, 5000);
		if(!selenium.isElementPresentAndVisible(date1Locator, 5000))
			throw new SolventSeleniumException("Date 1 is not available.");
		selenium.waitForAjax();
		selenium.type(date1Locator, Date1);
		selenium.waitForElementToBecomeVisible(date2Locator, 5000);
		if(!selenium.isElementPresentAndVisible(date2Locator, 5000))
			throw new SolventSeleniumException("Date 2 is not available.");
		selenium.waitForAjax();
		selenium.type(date2Locator, Date2);
		return this;
	}
	
	/**
	 * Method clicks the folder icon when searching for events by type
	 * @param attribute
	 * @param value
	 * @return
	 */
	public EventsByTypeSolvent clickEventFilterIcon() {
		boolean filterSuccess = false;
		for(int i = 0; i < 3 && !filterSuccess; i++){
			String nodeLocator = "//span[contains(., 'Filter')]";
			selenium.waitForAjax();
			selenium.waitForElementToBecomeVisible(nodeLocator, 5000);
			if(!selenium.isElementPresentAndVisible(nodeLocator, 5000))
				throw new SolventSeleniumException("Event filter icon is not available.");
			selenium.waitForAjax();
			selenium.click(nodeLocator);
			if(selenium.isTextPresent("error")){
				selenium.goBack();
			} else {
				filterSuccess = true;
			}
		}
		Assert.assertEquals("Filter icon was safely clicked", true, filterSuccess);
		return this;
	}
	
	/**
	 * Method clicks the folder icon when searching for events by type
	 * @param attribute
	 * @param value
	 * @return
	 */
	public EventsByTypeSolvent clickEventTypeFolderIcon() {
		boolean folderSuccess = false;
		for(int i = 0; i < 3 && !folderSuccess; i++){
			String nodeLocator = "//img[contains(@id, 'showPopupButton')]";
			selenium.waitForAjax();
			selenium.waitForElementToBecomeVisible(nodeLocator, 5000);
			if(!selenium.isElementPresentAndVisible(nodeLocator, 5000))
				throw new SolventSeleniumException("Event type folder is not available.");
			selenium.waitForAjax();
			selenium.click(nodeLocator);
			if(selenium.isTextPresent("error")){
				selenium.goBack();
			} else {
				folderSuccess = true;
			}
		}
		Assert.assertEquals("Folder icon was safely clicked", true, folderSuccess);
		return this;
	}
	
	/**
	 * Click any input/button based on its name. This method also supports button that doesn't have value attribute properly defined.
	 * Method will wait for button to appear if it doesn't appear then throw exception. If the Yukon error page is found, this button
	 * goes back and attempts to click it again.
	 * @param buttonName name of the input.
	 * @return
	 */
	public EventsByTypeSolvent clickButtonByExactNameSafely(String exactButtonName) {
		boolean buttonSuccess = false;
		for(int i = 0; i < 3 && !buttonSuccess; i++){
			String buttonLocator = "//input[@value='" + exactButtonName + "'][1]";
			selenium.isElementPresent(buttonLocator);
			selenium.waitForElementToBecomeVisible(buttonLocator, 9000);
			if(!selenium.isElementPresentAndVisible(buttonLocator, 9000))
				throw new SeleniumException("Unable to find '" + exactButtonName +"' to click at " + buttonLocator + " & waited 2000ms");
			selenium.click(buttonLocator);
			selenium.waitForPageToLoad();
			if(selenium.isTextPresent("error")){
				selenium.goBack();
				selenium.waitForPageToLoad();
				clickEventFilterIcon();
				new CommonSolvent().enterText("Event Log Date Range:", returnDate(EventsByTypeSolvent.initialDate));
			} else {
				buttonSuccess = true;
			}
		}
		Assert.assertEquals("Button was safely clicked", true, buttonSuccess);
		return this;
	}
	
	/**
	 * Method unselects all items from a multiListBox by its label name
	 * @param attribute
	 * @param value
	 * @return
	 */
	public EventsByTypeSolvent unselectAllMenuOptions(String label) {
		String nodeLocator = "//*[contains(., '" + label + "')]/following::select[1]";
		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(nodeLocator, 5000);
		if(!selenium.isElementPresentAndVisible(nodeLocator, 5000))
			throw new SolventSeleniumException(label+" is not available.");
		selenium.waitForAjax();
		selenium.removeAllSelections(nodeLocator);
		return this;
	}	

	/**
	 * This method is used in the events page to return all entries from a column from the table as an
	 * ArrayList of String.  This structure allows for easy iteration to verify all returned data.
	 * colNum = 1 corresponds to the Events column
	 * colNum = 2 corresponds to the Date and Time column
	 * colNum = 3 corresponds to the Message column
	 * @return finalLocator
	 */
	public ArrayList<String> returnColumnAsList(int colNum){
		String cellLocator, nextLocator;
		ArrayList<String> columnList = new ArrayList<String>();
		cellLocator = "tr[contains(@class, ' ') or contains(@class, ' altRow')]/td["+colNum+"]";
		nextLocator = "//div[contains(@class, 'content')]/following::tr[contains(@class, ' ') or contains(@class, ' altRow')]/td["+colNum+"]";
		try{
			while(selenium.waitForElementToBecomeVisible(nextLocator, 5000)){
				if(!selenium.getText(nextLocator).equals("No Results Found"))
					columnList.add(selenium.getText(nextLocator));
				nextLocator = nextLocator+"/following::"+cellLocator;
			}
		} catch(Exception e){ ; }
		return columnList;
	}	

}
