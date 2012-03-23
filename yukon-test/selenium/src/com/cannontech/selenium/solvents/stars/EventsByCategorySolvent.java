/**
 * 
 */
package com.cannontech.selenium.solvents.stars;

import java.util.ArrayList;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * Solvent method to handle stars events and category sections.
 * @author anuradha.uduwage
 *
 */
public class EventsByCategorySolvent extends AbstractSolvent {

	@Override
	public void prepare() {}

	/**
	 * Method returns the total number of returned events
	 * @param attribute
	 * @param value
	 * @return
	 */
	public int getTotalNumberOfEvents() {
		String nodeLocator = "//td[contains(@class, 'pageNumText')]";
		selenium.waitForElementToBecomeVisible(nodeLocator, 5000);
		if(!selenium.isElementPresentAndVisible(nodeLocator, 5000))
			throw new SolventSeleniumException("The number of events is not available.");
		selenium.waitForAjax();
		int eventTotal = 0;
		String event = selenium.getText(nodeLocator);
		String[] eventSplit = event.split(" ");
		for(int i = 0;  i < eventSplit.length; i++){
			if(eventSplit[i].equals("of"))
				try{
					eventTotal = Integer.parseInt(eventSplit[i+1]);
				} catch (Exception e) { e.printStackTrace(); }
		}
		return eventTotal;
	}
	
	/**
	 * Method unselects all items from a multiListBox by its label name
	 * @param attribute
	 * @param value
	 * @return
	 */
	public EventsByCategorySolvent unselectAllMenuOptions(String name) {
		String nodeLocator = "//select[@name='" + name + "']";
		selenium.waitForElementToBecomeVisible(nodeLocator, 5000);
		if(!selenium.isElementPresentAndVisible(nodeLocator, 5000))
			throw new SolventSeleniumException(name+" menu is not available.");
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
		nextLocator = "//table[contains(@class, 'compactResultsTable')]//following::tr[contains(@class, ' ') or contains(@class, ' altRow')]/td["+colNum+"]";
		while(selenium.isElementPresent(nextLocator, 5000)){
			columnList.add(selenium.getText(nextLocator));
			nextLocator = nextLocator+"/following::"+cellLocator;
		}
		return columnList;
	}	
}
