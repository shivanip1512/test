package com.cannontech.selenium.test.stars;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.*;
import com.cannontech.selenium.solvents.stars.EventsByCategorySolvent;

/**
 * This test case is the first in a series of test cases that verifies the new 
 * functionality of  Events, By Category and By Type – which now contains detailed 
 * information for STARS events.  It has been included in the  View Log  area from 
 * the Operations > Administration page.  It allows for the Stars operator to gather
 * more detailed history of Stars events initiated by the operator and the customer.  
 * Events include  creating, editing and deleting of accounts and devices; changing
 * account information; changing Contact information; enrollment and unenrollment; 
 * thermostat Schedule creation and edits, and Opt Out history.
 * Also, these test cases should be executed after other STARS test cases (Regression
 * and New Features) have completed.  Executing those tests will populate table(s)
 * with information needed for these test cases.
 * Covers 3.2.1.18 - 3.2.1.24
 * @author ricky.jones
 */
public class TestY01EventsByCategorySelenium extends SolventSeleniumTestCase {
	//This variable sets the Event Log initial date X days from the current
	final int initialDate = -365;
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		//use the LoginSolvent to login
		start();
		LoginLogoutSolvent loginLogoutSolvent = new LoginLogoutSolvent();
		loginLogoutSolvent.cannonLogin("starsop", "starsop");		
	}
	/**
     * Method returns the given date offset by fromCurrent
     * @param fromCurrent
     * @return
     */
    private String returnDate(int fromCurrent){
    	   Calendar cal = Calendar.getInstance();
    	   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    	   cal.add(Calendar.DATE, fromCurrent);
    	   return dateFormat.format(cal.getTime());
    }
    /**
	 * This test case will verify the search and filter functionality of  Events > By Category
	 * for all stars events
	 */
	@Test
	public void allStarsEvents() {
			init();
			EventsByCategorySolvent local = new EventsByCategorySolvent();
			CommonSolvent common = new CommonSolvent();
			YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
			common.clickLinkByName("View Logs");
			
			topmenu.clickTopMenuItem("Events");
			Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
			Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
			Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
			common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
			
			//Oper Events: Verify Category = system.login, Filter Value = starsop
			common.selectDropDownMenu("eventLog/viewByCategory", "categories", "system.login");
			common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
			common.clickButtonByNameWithPageLoadWait("Filter");
			ArrayList<String> messages = local.returnColumnAsList(3);
			for(int i = 0; i < messages.size(); i++){
				Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
			}
			
			//Cust Events: Verify Filter Value = 71000005
			common.enterInputText("eventLog/viewByCategory", "filterValue", "71000005");
			common.clickButtonByNameWithPageLoadWait("Filter");
			messages = local.returnColumnAsList(3);
			for(int i = 0; i < messages.size(); i++){
				Assert.assertEquals(messages.get(i)+" is from 71000005", true, messages.get(i).contains("71000005"));
			}
			
			//All Stars Events: Verify Categories = stars
			common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars");
			common.enterInputText("eventLog/viewByCategory", "filterValue", " ");
			common.clickButtonByNameWithPageLoadWait("Filter");
			messages = local.returnColumnAsList(1);
			for(int i = 0; i < messages.size(); i++){
				Assert.assertEquals(messages.get(i)+" is from stars", true, messages.get(i).contains("stars"));
			}
			//int allStarsTotal = local.getTotalNumberOfEvents();
			
			//All Stars Events using account: Verify Categories = stars.account
			common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account");
			common.enterInputText("eventLog/viewByCategory", "filterValue", " ");
			common.clickButtonByNameWithPageLoadWait("Filter");
			//int AccountStarsTotal = local.getTotalNumberOfEvents();
			//Assert.assertEquals(allStarsTotal, AccountStarsTotal);

			//Accounts w/ account #: Verify Categories = stars.account, Filter Value = 71000005
			common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account");
			common.enterInputText("eventLog/viewByCategory", "filterValue", "71000005");
			common.clickButtonByNameWithPageLoadWait("Filter");
			messages = local.returnColumnAsList(3);
			for(int i = 0; i < messages.size(); i++){
				Assert.assertEquals(messages.get(i)+" is from 71000005", true, messages.get(i).contains("71000005"));
			}
			common.end();
	}
	
	/**
	 * This test case will verify the search and filter functionality of  Events > By Category
	 * for stars.account.optOut 
	 */
	@Test
	public void starsAccountOptOut() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//Opt out only: Verify Category = stars.account.optOut
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.optOut");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from stars.account.optOut", true, messages.get(i).contains("stars.account.optOut"));
		}
		
		//Opt out w/starsop: Verify Category = stars.account.optOut, Filter Value = starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.optOut");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
		}

		//Opt out w/71000015: Verify Category = stars.account.optOut, Filter Value = 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.optOut");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000015", true, messages.get(i).contains("71000015"));
		}

		//Opt out w/711200151: Verify Category = stars.account.optOut, Filter Value = 711200151
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.optOut");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "711200151");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 711200151", true, messages.get(i).contains("711200151"));
		}
		common.end();
	}

	/**
	 * This test case will verify the search and filter functionality of  Events > By Category
	 * for stars.account.enrollment  
	 */
	@Test
	public void starsAccountEnrollment() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//Enrollment only: Verify Category = stars.account.enrollment
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.enrollment");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from stars.account.enrollment", true, messages.get(i).contains("stars.account.enrollment"));
		}
		
		//Enrollment w/starsop: Verify Category = stars.account.enrollment, Filter Value = starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.enrollment");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
		}

		//Enrollment w/71000015: Verify Category = stars.account.enrollment, Filter Value = 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.enrollment");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000015", true, messages.get(i).contains("71000015"));
		}

		//Enrollment w/LMProgram-29: Verify Category = stars.account.enrollment, Filter Value = LMProgram-29
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.enrollment");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "LMProgram-29");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from LMProgram-29", true, messages.get(i).contains("LMProgram-29"));
		}
		common.end();
	}

	/**
	 * This test case will verify the search and filter functionality of  Events > By Category
	 * for stars.account.appliance  
	 */
	@Test
	public void starsAccountAppliance() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//Appliance only: Verify Category = stars.account.appliance
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.appliance");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from stars.account.appliance", true, messages.get(i).contains("stars.account.appliance"));
		}
		
		//Appliance w/app name: Verify Category = stars.account.appliance, Filter Value = AC
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.appliance");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "AC");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from AC", true, messages.get(i).contains("AC"));
		}

		//Appliance w/app name: Verify Category = stars.account.appliance, Filter Value = AC – Stars Import
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.appliance");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "AC - Stars Import");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from AC – Stars Import", true, messages.get(i).contains("AC – Stars Import"));
		}

		//Appliance w/71000015: Verify Category = stars.account.appliance, Filter Value = 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.appliance");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}

	/**
	 * This test case will verify the search and filter functionality of  Events > By Category
	 * for stars.account.contactInfo. 
	 */
	@Test
	public void starsAccountContactInfo() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//ContactInfo only: Verify Category = stars.account.contactInfo
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.contactInfo");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from stars.account.contactInfo", true, messages.get(i).contains("stars.account.contactInfo"));
		}
		
		//ContactInfo w/starsop: Verify Category = stars.account.contactInfo, Filter Value = starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.contactInfo");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
		}

		//ContactInfo w/First QATest: Verify Category = stars.account.contactInfo, Filter Value = First QATest
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.contactInfo");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "First QATest");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from First QATest", true, messages.get(i).contains("First QATest") || messages.get(i).contains("QATest, First"));
		}

		//ContactInfo w/71000015: Verify Category = stars.account.contactInfo, Filter Value = 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.contactInfo");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}
	
	/**
	 * This test case will verify the search and filter functionality of  Events > By Category 
	 * for stars.account.thermostat. 
	 */
	@Test
	public void starsAccountThermostat() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//Thermostat only: Verify Category = stars.account.thermostat
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.thermostat");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from stars.account.thermostat", true, messages.get(i).contains("stars.account.thermostat"));
		}
		
		//Thermostat w/Sched01-15: Verify Category = stars.account.thermostat, Filter Value = Sched01-15
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.thermostat");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "Sched01-15");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from Sched01-15", true, messages.get(i).contains("Sched01-15"));
		}

		//Thermostat w/starsop: Verify Category = stars.account.thermostat, Filter Value = starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.thermostat");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
		}

		//Thermostat w/71000015: Verify Category = stars.account.thermostat, Filter Value = 71000015
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "stars.account.thermostat");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000015");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000015", true, messages.get(i).contains("71000015"));
		}
		common.end();
	}
	
	/**
	 * This test case will verify the search and filter functionality of  Events > By Category 
	 * for hardware. 
	 */
	@Test
	public void Hardware() {
		init();
		EventsByCategorySolvent local = new EventsByCategorySolvent();
		CommonSolvent common = new CommonSolvent();
		YukonTopMenuSolvent topmenu = new YukonTopMenuSolvent();
		common.clickLinkByName("View Logs");
		
		topmenu.clickTopMenuItem("Events");
		Assert.assertEquals("Categories is displayed.", true, common.isTextPresent("Categories"));
		Assert.assertEquals("Filter Value is displayed.", true, common.isTextPresent("Filter Value"));
		Assert.assertEquals("Date Range is displayed.", true, common.isTextPresent("Date Range"));
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//YUK-????
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "hardware");
		int earlierStartDate = initialDate;
		boolean updateAttemptedFound = false;
		//Searches back up to 2 years for hardware.hardwareCreationAttemptedByOperator to appear
		//changed to  Searches back up to 2 years for hardware.hardwareAdded to appear
		for(int j = 0; j < 12 && !updateAttemptedFound; j++){
			earlierStartDate -= 64;
			common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(earlierStartDate));
			common.clickButtonByNameWithPageLoadWait("Filter");
			ArrayList<String> messages = local.returnColumnAsList(1);
			for(int i = 0; i < messages.size(); i++){
				if(messages.get(i).contains("hardware.hardwareAdded"))
					updateAttemptedFound = true;
			}
		}
		Assert.assertEquals("hardware.hardwareAdded has been found", true, updateAttemptedFound);
		common.enterInputText("eventLog/viewByCategory", "startDate", returnDate(initialDate));
		
		//Hardware only: Verify Category = hardware
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "hardware");
		common.clickButtonByNameWithPageLoadWait("Filter");
		ArrayList<String> messages = local.returnColumnAsList(1);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from hardware", true, messages.get(i).contains("hardware"));
		}
		
		//Hardware w/711200151: Verify Category = hardware, Filter Value = 711100141
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "hardware");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "711100141");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 711100141", true, messages.get(i).contains("711100141"));
		}

		//Hardware w/starsop: Verify Category = hardware, Filter Value = starsop
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "hardware");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "starsop");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from starsop", true, messages.get(i).contains("starsop"));
		}

		//Hardware w/71000015: Verify Category = hardware, Filter Value = 71000014
		common.selectDropDownMenu("eventLog/viewByCategory", "categories", "hardware");
		common.enterInputText("eventLog/viewByCategory", "filterValue", "71000014");
		common.clickButtonByNameWithPageLoadWait("Filter");
		messages = local.returnColumnAsList(3);
		for(int i = 0; i < messages.size(); i++){
			Assert.assertEquals(messages.get(i)+" is from 71000014", true, messages.get(i).contains("71000014"));
		}
		common.end();
	}
}