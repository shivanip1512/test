package com.cannontech.selenium.test.stars;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.*;
import com.cannontech.selenium.solvents.stars.EventsByTypeSolvent;
/**
 * This test case is the first in a series of test cases that verifies the new 
 * functionality of Events, By Category and By Type – which now contains detailed 
 * information for STARS events. It has been included in the View Log area from 
 * the Operations > Administration page. It allows for the Stars operator to gather
 * more detailed history of Stars events initiated by the operator and the customer. 
 * Events include creating, editing and deleting of accounts and devices; changing
 * account information; changing Contact information; enrollment and unenrollment; 
 * thermostat Schedule creation and edits, and Opt Out history.
 * Also, these test cases should be executed after other STARS test cases (Regression
 * and New Features) have completed. Executing those tests will populate table(s)
 * with information needed for these test cases.
 * Covers 3.2.1.25 - 3.2.1.44
 * @author ricky.jones
 */
public class TestY03EventsByTypeSelenium extends SolventSeleniumTestCase {
	//This variable sets the Event Log initial date X days from the current
	final static int initialDate = -32;
	//These variables set the start and stop opt out dates X days from the current
	final int optOutStartDate1 = -21;
	final int optOutStartDate2 = -7;
	final int optOutStopDate1 = -21;
	final int optOutStopDate2 = -7;
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation page.
	 */
	public void init() {
		//use the LoginSolvent to login
		start();
		new LoginLogoutSolvent().cannonLogin("starsop", "starsop");		
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type
	 * for system.login.loginWeb. 
	 */
	@Test
	public void systemLoginWeb() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		
		common.clickLinkByName("View Logs");
		//Collect Data from By Category searches to compare later to By Event searches
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", eventsByType.returnDate(initialDate));
			
		//Category = system.login.loginWeb
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "system.login");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> loginWebCAT = eventsByType.returnColumnAsList(2);

		//Category = system.login, Filter Value: starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "system.login");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> loginStarsopCAT = eventsByType.returnColumnAsList(2);

		//Category = system.login, Filter Value: 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "system.login");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> loginCustCAT = eventsByType.returnColumnAsList(2);

		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("system");
		yuktree.expandChildByParent("system", "login");
		yuktree.selectTreeNode("loginWeb");
		//Assert.assertTrue(common.isTextPresent("system.login.loginWeb"));
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		//Assert.assertTrue(common.isTextPresent("system.login.loginWeb"));
		ArrayList<String> loginWebTYP = eventsByType.returnColumnAsList(1);
		//Verify results are similar to results from ByCategory > system.login.loginWeb
		for(int i = 0; i < loginWebTYP.size(); i++){
			Assert.assertEquals(loginWebTYP.get(i), true, loginWebTYP.get(i).contains(loginWebCAT.get(i)));
		}
			
			
		//starsop Events: Filter Value = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		//Assert.assertTrue(common.isTextPresent("system.login.loginWeb"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		ArrayList<String> loginStarsopTYP = eventsByType.returnColumnAsList(1);
		//Verify results are similar to results from ByCategory > system.login.loginWeb, Filter Value = starsop
		for(int i = 0; i < loginStarsopTYP.size(); i++){
			Assert.assertEquals(loginStarsopTYP.get(i), true, loginStarsopTYP.get(i).contains(loginStarsopCAT.get(i)));
		}
			
		//Cust Events: Filter Value = 51000000
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		//Assert.assertTrue(common.isTextPresent("system.login.loginWeb"));
		messages = eventsByType.returnColumnAsList(2);
		ArrayList<String> addresses = eventsByType.returnColumnAsList(3);
		String customerIP = null;
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
			customerIP = addresses.get(i);
		}
//		ArrayList<String> loginCustTYP = eventsByType.returnColumnAsList(1);
//		//Verify results are similar to results from ByCategory > system.login.loginWeb, Filter Value = 71000015
//		for(int i = 0; i < loginCustTYP.size(); i++){
//			Assert.assertEquals(loginCustTYP.get(i), true, loginCustTYP.get(i).contains(loginCustCAT.get(i+4)));
//		}
			
		if(customerIP != null){
			//Cust Events: Remote Address = customerIP
			common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
			common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", " ");
			common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", customerIP);
			common.clickButtonByNameWithPageLoadWait("Filter");
			//Assert.assertTrue(common.isTextPresent("system.login.loginWeb"));
			ArrayList<String> loginRemoteTYP = eventsByType.returnColumnAsList(1);
			topmenu.clickTopSubMenuItem("Category");
			//Category = system.login.loginWeb, Filter Value = customerIP
			common.selectDropDownMenu("eventLog/viewByCategory", "categories", "system.login");
			common.enterInputText("eventLog/viewByCategory", "filterValue", customerIP);
			common.enterInputText("eventLog/viewByCategory", "startDate", eventsByType.returnDate(initialDate));
			common.clickButtonByNameWithPageLoadWait("Filter");
			ArrayList<String> loginRemoteCAT = eventsByType.returnColumnAsList(2);
			for(int i = 0; i < loginRemoteTYP.size(); i++){
				Assert.assertEquals(loginRemoteTYP.get(i), true, loginRemoteTYP.get(i).contains(loginRemoteCAT.get(i)));
			}
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type
	 * for stars.account.accountAdded
	 */
	@Test
	public void starsAccountAdded() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: accountAdded
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.selectTreeNode("accountAdded");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountAdded"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountAdded"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000010
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000010");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000010", true, messages.get(i).contains("71000010"));
		}
		
		//AccountCreationAttemptedThroughAccountImporter: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("accountCreationAttemptedThroughAccountImporter");
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountCreationAttemptedThroughAccountImporter"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//AccountCreationAttemptedThroughAccountImporter: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountCreationAttemptedThroughAccountImporter"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//AccountCreationAttemptedThroughAccountImporter: Username = starsop, Account Number = 71000012
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000012");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountCreationAttemptedThroughAccountImporter"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000012", true, messages.get(i).contains("71000012"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type
	 * for stars.account.accountDeleted
	 */
	@Test
	public void starsAccountDeleted() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: accountDeleted
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.selectTreeNode("accountDeleted");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeleted"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeleted"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 51000003
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000003");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeleted"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000003", true, messages.get(i).contains("51000003"));
		}
		
		//AccountDeletionAttemptedThroughAccountImporter: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("accountDeletionAttemptedThroughAccountImporter");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeletionAttemptedThroughAccountImporter"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//AccountDeletionAttemptedThroughAccountImporter: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeletionAttemptedThroughAccountImporter"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//AccountDeletionAttemptedThroughAccountImporter: Username = starsop, Account Number = 41000000
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "41000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.accountDeletionAttemptedThroughAccountImporter"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 41000000", true, messages.get(i).contains("41000000"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type
	 * for stars.account.thermostat.thermostatScheduleSaved 
	 */
	@Test
	public void starsAccountThermostat() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: thermostatScheduleSaved
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "thermostat");
		yuktree.selectTreeNode("thermostatScheduleSaved");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatScheduleSaved"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}

		//Cust Events: Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatScheduleSaved"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		
		//AccountDeletionAttemptedThroughAccountImporter: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("thermostatScheduleSavingAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatScheduleSavingAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//thermostatScheduleSavingAttemptedByOperator: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatScheduleSavingAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type
	 * for stars.account.thermostat.thermostatManuallySet. 
	 * NO DATA FOR THIS BY OPERATOR YET
	 */
	@Test
	public void starsAccountThermostatManuallySet() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: thermostat.thermostatManuallySet
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "thermostat");
		yuktree.selectTreeNode("thermostatManuallySet");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManuallySet"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManuallySet"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManuallySet"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
				
		//ThermostatManualSetAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("thermostatManualSetAttemptedByOperator");
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManualSetAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//thermostatManualSetAttemptedByOperator: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManualSetAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//thermostatManualSetAttemptedByOperator: Username = starsop, Account Number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManualSetAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Serial number = 711100152
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711100152");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatManualSetAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100152", true, messages.get(i).contains("711100152"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Category 
	 * for stars.account.thermostat.thermostatLabelChanged 
	 */
	@Test
	public void starsAccountThermostatLabelChanged() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: thermostat.thermostatLabelChanged
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "thermostat");
		yuktree.selectTreeNode("thermostatLabelChanged");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatLabelChanged"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "711100131");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatLabelChanged"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100131", true, messages.get(i).contains("711100131"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711100131");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatLabelChanged"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Serial number = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711100131");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "711100131a");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.thermostat.thermostatLabelChanged"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100131", true, messages.get(i).contains("711100131"));
		}
		messages = eventsByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100131a", true, messages.get(i).contains("711100131a"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.contactInfo.contactAdded 
	 */
	@Test
	public void contactAdded() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.contactInfo.contactAdded
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "contactInfo");
		yuktree.selectTreeNode("contactAdded");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactAdded"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactAdded"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Contact Name = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "Second QATest");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is Second QATest", true, messages.get(i).contains("Second QATest"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.contactInfo.contactUpdated 
	 */
	@Test
	public void contactUpdated() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.contactInfo.contactUpdated
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "contactInfo");
		yuktree.selectTreeNode("contactUpdated");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactUpdated"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactUpdated"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 51000000
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactUpdated"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 51000000, Contact Name = First QATest
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "First QATest");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.contactInfo.contactUpdated"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is First QATest", true, messages.get(i).contains("First QATest"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.enrollment.deviceEnrolled 
	 */
	@Test
	public void deviceEnrolled() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.enrollment.deviceEnrolled
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "enrollment");
		yuktree.selectTreeNode("deviceEnrolled");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200151");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151, Program Name = LMProgram-27
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200151");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "LdPgm01-2Gear");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		messages = eventsByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdPgm01-2Gear", true, messages.get(i).contains("LdPgm01-2Gear"));
		}

		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151, Program Name = LMProgram-27
		//			Load Group Name = Versacom-13
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200151");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.filterValue", "LdGrp01-Expresscom");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceEnrolled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		messages = eventsByType.returnColumnAsList(5);
		//for(int i = 0; i < messages.size(); i++){
		//	Assert.assertEquals(messages.get(i)+" is LMProgram-27", true, messages.get(i).contains("LMProgram-27"));
		//}
		messages = eventsByType.returnColumnAsList(6);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdGrp01-Expresscom", true, messages.get(i).contains("LdGrp01-Expresscom"));
		}
		
		//enrollmentAttemptedByConsumer: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("enrollmentAttemptedByConsumer");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.enrollmentAttemptedByConsumer"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.enrollmentAttemptedByConsumer"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.enrollment.deviceUnenrolled 
	 */
	@Test
	public void deviceUnenrolled() {
		init();
		EventsByTypeSolvent eventByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.enrollment.deviceUnenrolled
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "enrollment");
		yuktree.selectTreeNode("deviceUnenrolled");
		ArrayList<String> initialAdded = eventByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventByType.returnDate(initialDate));
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		ArrayList<String> rangeAdded = eventByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		ArrayList<String> messages = eventByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = 51000000a
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "51000000a");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		messages = eventByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000a", true, messages.get(i).contains("51000000a"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		messages = eventByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000015");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200151");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		messages = eventByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		messages = eventByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		
		//Cust Events: Program Name = LdPgm01-2Gear)
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "LdPgm02-2Gear");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		messages = eventByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdPgm02-2Gear", true, messages.get(i).contains("LdPgm02-2Gear"));
		}

		//Cust Events: Program Name = LMProgram-27, Load Group Name = Expresscom-22 (Stars Import)
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "LdPgm01-2Gear");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "LdGrp01-Versacom");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.deviceUnenrolled"));
		messages = eventByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdPgm02-2Gear", true, messages.get(i).contains("LdPgm02-2Gear"));
		}
		messages = eventByType.returnColumnAsList(6);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdGrp01-Versacom", true, messages.get(i).contains("LdGrp01-Versacom"));
		}
		
		//stars.account.enrollment.unenrollmentAttemptedByConsumer: All events
		eventByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("unenrollmentAttemptedByConsumer");
		waitFiveSeconds();
		initialAdded = eventByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventByType.returnDate(initialDate));
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.unenrollmentAttemptedByConsumer"));
		rangeAdded = eventByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = 51000000
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "51000000");
		eventByType.clickButtonByExactNameSafely("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.enrollment.unenrollmentAttemptedByConsumer"));
		messages = eventByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.optOut.deviceOptedOut 
	 */
	@Test
	public void deviceOptedOut() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.optOut.deviceOptedOut
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "optOut");
		yuktree.selectTreeNode("deviceOptedOut");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = 71000014
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "71000014");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000014
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000014");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000014");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711100141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100141", true, messages.get(i).contains("711100141"));
		}
		
		//w/Dates: Start1 = -21 days, Start2 = -7 days, Stop1 = -21 days, Stop2 = -7 days
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.startDate", eventsByType.returnDate(optOutStartDate1));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.stopDate", eventsByType.returnDate(optOutStartDate2));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.startDate", eventsByType.returnDate(optOutStopDate1));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.stopDate", eventsByType.returnDate(optOutStopDate2));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		messages = eventsByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			String[] optOutSplit = messages.get(i).split(" ");
			String optOutDate = optOutSplit[0];
			Assert.assertEquals(optOutDate+" is within "+optOutStartDate1+" to "+optOutStartDate2+" days ago",
					true, eventsByType.dateWithinRange(optOutDate, eventsByType.returnDate(optOutStartDate1), eventsByType.returnDate(optOutStartDate2)));
		}
		messages = eventsByType.returnColumnAsList(6);
		for(int i = 0; i < messages.size(); i++){
			String[] optOutSplit = messages.get(i).split(" ");
			String optOutDate = optOutSplit[0];
			Assert.assertEquals(optOutDate+" is within "+optOutStopDate1+" to "+optOutStopDate2+" days ago",
					true, eventsByType.dateWithinRange(optOutDate, eventsByType.returnDate(optOutStopDate1), eventsByType.returnDate(optOutStopDate2)));
		}
		
		//w/ Error Dates: Start1 = -7 days, Start2 = -21 days, Stop1 = -7 days, Stop2 = -21 days
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.startDate", eventsByType.returnDate(optOutStartDate2));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.stopDate", eventsByType.returnDate(optOutStartDate1));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.startDate", eventsByType.returnDate(optOutStopDate2));
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.stopDate", eventsByType.returnDate(optOutStopDate1));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.deviceOptedOut"));
		Assert.assertEquals("Error occurred with bad dates", true, common.isTextPresent("Errors found, check fields."));

		//stars.account.optOut.optOutAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("optOutAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.optOut.optOutCanceled
	 */
	@Test
	public void optOutCanceled() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.optOut.optOutCanceled
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "optOut");
		yuktree.selectTreeNode("optOutCanceled");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCanceled"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCanceled"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = 51000000a
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCanceled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000014");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCanceled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Name = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000014");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCanceled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200141", true, messages.get(i).contains("711200141"));
		}
		
		//after type Jira fixed (yuk-9455) correct spelling
		//stars.account.optOut.optOutCancelAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("optOutCancelAttemptedByOperator");
		waitFiveSeconds();
		//yuktree.selectTreeNode("optOutCancelAttemptedByOperator");
		//common.isTextPresent("stars.account.optOut.optOutCancelAttemptedByOperator");
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCancelAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutCancelAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.optOut.optOutLimitReset
	 */
	@Test
	public void optOutLimitReset() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.optOut.optOutLimitReset
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "optOut");
		yuktree.selectTreeNode("optOutLimitReset");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitReset"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitReset"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitReset"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000000, Device Name = 711100001
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711100001");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitReset"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		
		//stars.account.optOut.optOutLimitResetAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("optOutLimitResetAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitResetAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.optOut.optOutLimitResetAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.appliance.applianceAdded
	 */
	@Test
	public void applianceAdded() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.appliance.applianceAdded
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "appliance");
		yuktree.selectTreeNode("applianceAdded");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Appliance Type = AC
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "AC");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is AC", true, messages.get(i).contains("AC"));
		}
		
		//Cust Events: Username = starsop, Account number = 51000000, Appliance Type = AC, Device Name = 511200001
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "AC");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "511200001");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is AC", true, messages.get(i).contains("AC"));
		}
		messages = eventsByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 511200001", true, messages.get(i).contains("511200001"));
		}
		
		//Cust Events: Program Name = LMProgram-28
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.filterValue", "LdPgm01-2Gear");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdded"));
		messages = eventsByType.returnColumnAsList(6);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LMProgram-28", true, messages.get(i).contains("LdPgm01-2Gear"));
		}
		
		//stars.account.appliance.applianceAdditionAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("applianceAdditionAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdditionAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceAdditionAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for stars.account.appliance.applianceDeleted
	 */
	@Test
	public void applianceDeleted() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: stars.account.appliance.applianceDeleted
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("stars");
		yuktree.expandChildByParent("stars", "account");
		yuktree.expandChildByParent("account", "appliance");
		yuktree.selectTreeNode("applianceDeleted");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Appliance Type = AC
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "AC");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is AC", true, messages.get(i).contains("AC"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Appliance Type = AC, Device Name = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "51000000");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "AC");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", "511200001");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 51000000", true, messages.get(i).contains("51000000"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is AC", true, messages.get(i).contains("AC"));
		}
		messages = eventsByType.returnColumnAsList(5);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 511200001", true, messages.get(i).contains("511200001"));
		}
		
		//Cust Events: Program Name = LdPgm01-2Gear
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[3].filterValue.filterValue", " ");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[4].filterValue.filterValue", "LdPgm01-2Gear");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeleted"));
		messages = eventsByType.returnColumnAsList(6);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is LdPgm01-2Gear", true, messages.get(i).contains("LdPgm01-2Gear"));
		}
		
		//stars.account.appliance.applianceDeletionAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("applianceDeletionAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeletionAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("stars.account.appliance.applianceDeletionAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for hardware.hardwareAdded
	 */
	@Test
	public void hardwareAdded() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: hardware.hardwareAdded
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("hardware");
		yuktree.selectTreeNode("hardwareAdded");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdded"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdded"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200141", true, messages.get(i).contains("711200141"));
		}
		
		//Cust Events: Username = starsop, Account number = 71000015, Device Label = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "71000014");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "711200141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdded"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200141", true, messages.get(i).contains("711200141"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000014", true, messages.get(i).contains("71000014"));
		}
		
		//hardware.hardwareAdditionAttemptedThroughApi: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("hardwareAdditionAttemptedThroughApi");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdditionAttemptedThroughApi"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareAdditionAttemptedThroughApi"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for hardware.hardwareCreated
	 */
	@Test
	public void hardwareCreated() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: hardware.hardwareCreated
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("hardware");
		yuktree.selectTreeNode("hardwareCreated");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareCreated"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareCreated"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Device Label = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711200151");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareCreated"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		
		//hardware.hardwareCreationAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("hardwareCreationAttemptedByOperator");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareCreationAttemptedByOperator"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareCreationAttemptedByOperator"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for hardware.hardwareRemoved
	 */
	@Test
	public void hardwareRemoved() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: hardware.hardwareRemoved
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("hardware");
		yuktree.selectTreeNode("hardwareRemoved");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemoved"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemoved"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Device Label = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "91000010");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemoved"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 91000010", true, messages.get(i).contains("91000010"));
		}
		
		//Cust Events: Username = starsop, Device Label = 711200151, Account Number = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "91000010");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "71000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemoved"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 91000010", true, messages.get(i).contains("91000010"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		
		//hardware.hardwareCreationAttemptedByOperator: All events
		eventsByType.clickEventTypeFolderIcon();
		yuktree.selectTreeNode("hardwareRemovalAttemptedThroughAccountImporter");
		waitFiveSeconds();
		initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemovalAttemptedThroughAccountImporter"));
		rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//Cust Events: Username = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711100141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.hardwareRemovalAttemptedThroughAccountImporter"));
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711100141", true, messages.get(i).contains("711100141"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for hardware.config.hardwareEnabled
	 */
	@Test
	public void hardwareEnabled() {
		init();
		EventsByTypeSolvent eventsByType = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: hardware.config.hardwareEnabled
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		eventsByType.clickEventTypeFolderIcon();
		yuktree.expandByName("hardware");
		yuktree.expandChildByParent("hardware", "config");
		yuktree.selectTreeNode("hardwareEnabled");
		ArrayList<String> initialAdded = eventsByType.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", eventsByType.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareEnabled"));
		ArrayList<String> rangeAdded = eventsByType.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareEnabled"));
		ArrayList<String> messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Serial Number = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711200151");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareEnabled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		
		//Cust Events: Username = starsop, Serial Number = 711200151, Account Number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "711200151");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareEnabled"));
		messages = eventsByType.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = eventsByType.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 711200151", true, messages.get(i).contains("711200151"));
		}
		messages = eventsByType.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}
	/**
	 * This test case will verify the search and filter functionality of Events > By Type 
	 * for hardware.config.hardwareConfigUpdated
	 */
	@Test
	public void hardwareConfigUpdated() {
		init();
		EventsByTypeSolvent local = new EventsByTypeSolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		YukonTreeSolvent yuktree = new YukonTreeSolvent();
		common.clickLinkByName("View Logs");
		
		//Event Type: hardware.config.hardwareConfigUpdated
		topmenu.clickTopMenuItem("Events");
		topmenu.clickTopSubMenuItem("Type");
		Assert.assertTrue(common.isPageTitle("Event Log"));
		local.clickEventTypeFolderIcon();
		yuktree.expandByName("hardware");
		yuktree.expandChildByParent("hardware", "config");
		yuktree.selectTreeNode("hardwareConfigUpdated");
		ArrayList<String> initialAdded = local.returnColumnAsList(1);
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "startDate", local.returnDate(initialDate));
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareConfigUpdated"));
		ArrayList<String> rangeAdded = local.returnColumnAsList(1);
		//YUK-9219
		for(int i = 0; i < initialAdded.size(); i++){
			Assert.assertEquals(initialAdded.get(i)+" is present in the range results.", true, rangeAdded.contains(initialAdded.get(i)));
		}
		
		//starsop Events: Username = starsop
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareConfigUpdated"));
		ArrayList<String> messages = local.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		
		//Cust Events: Username = starsop, Serial Number = 711200151
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "91000010");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareConfigUpdated"));
		messages = local.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 91000010", true, messages.get(i).contains("91000010"));
		}
		
		//Cust Events: Username = starsop, Serial Number = 711200151, Account Number = 71000015
		common.clickButtonBySpanTextWithElementWait("Filter","//div[@id='filterPopup']");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[0].filterValue.filterValue", "starsop");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[1].filterValue.filterValue", "91000010");
		common.enterInputText("eventLog/viewByType", "eventLogFilters[2].filterValue.filterValue", "71000000");
		common.clickButtonByNameWithPageLoadWait("Filter");
		Assert.assertTrue(common.isTextPresent("hardware.config.hardwareConfigUpdated"));
		messages = local.returnColumnAsList(2);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is starsop", true, messages.get(i).contains("starsop"));
		}
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 91000010", true, messages.get(i).contains("91000010"));
		}
		messages = local.returnColumnAsList(4);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is 71000000", true, messages.get(i).contains("71000000"));
		}
		common.end();
	}
}
