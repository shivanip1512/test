package com.cannontech.selenium.test.metering;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.*;
import com.cannontech.selenium.solvents.metering.*;
import com.cannontech.selenium.test.metering.MeteringHelper;


/**
 * This test verifies that scheduled scripts can be enabled/disabled, ran and stopped,
 * and run from set start and stop times
 * @author ricky.jones
 * and refactored by
 * @author anuradha.uduwage
 */
public class TestScheduledScriptsSelenium extends SolventSeleniumTestCase {
	/**
	 * Test method logs in as yukon, yukon and check all the links in navigation
	 * page.
	 */
	public void init() {
		//use the LoginSolvent to login
		setCustomURL("http://pspl-qa008.cannontech.com:8080");//TODO make a scheduled script on the reg machines so this script can run on them
		start();
		new LoginLogoutSolvent().cannonLogin("yukon", "yukon");		
	}
	/**
	 * This test disables and enables a test script
	 */
	@Test
	public void toggleScheduledScript() {
		init();
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();
		MeteringHelper help = new MeteringHelper();
		
		help.navigateToScriptsPage();
		Assert.assertEquals("Verify MCT-410 Auto Test Exists", true, common.isTextPresent("MCT-410 Auto Test"));
		Assert.assertEquals("Verify MCT-410 Auto Test is initially waiting", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Waiting"));
		
		//Disable schedule
		table.clickDisableByScheduleName("MCT-410 Auto Test");
		Assert.assertEquals("Verify MCT-410 Auto Test isEnabled is false", false, table.isScheduleEnable("MCT-410 Auto Test"));
		Assert.assertEquals("Verify MCT-410 Auto Test's state is disabled", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Disabled"));	
	
		
		//Enable schedule
		table.clickEnableByScheduleName("MCT-410 Auto Test");
		Assert.assertEquals("Verify MCT-410 Auto Test's final state is waiting", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Waiting"));	
		Assert.assertEquals("Verify MCT-410 Auto Test is enabled", true, table.isScheduleEnable("MCT-410 Auto Test"));
		common.end();
	}
	/**
	 * This test runs and stops a test script
	 */
	@Test
	public void runScheduledScript() {
		init();
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent table = new ScheduleScriptsTableSolvent();
		MeteringHelper help = new MeteringHelper();
		
		help.navigateToScriptsPage();
		Assert.assertEquals("Verify MCT-410 Auto Test Exists", true, common.isTextPresent("MCT-410 Auto Test"));
		Assert.assertEquals("Verify MCT-410 Auto Test is initially waiting", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Waiting"));
		
		//Run test script
		help.runSchedule("MCT-410 Auto Test");
		Assert.assertEquals("Verify test returned to scheduled scripts page", true, common.isTextPresent("Scheduled Scripts"));
		Assert.assertEquals("Verify MCT-410 Auto Test is running", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Running"));
		
		//Stop test script
		help.stopSchedule("MCT-410 Auto Test");
		Assert.assertEquals("Verify MCT-410 Auto Test's final state is waiting", true, table.getCurrentStateBySchedule("MCT-410 Auto Test").equals("Waiting"));
		common.end();
	}
	/**
	 * This test sets a test script to run one minute from the current time and end two minutes after current time
	 */
	@Test
	public void setScheduledScriptTime() {
		init();
		CommonSolvent common = new CommonSolvent();
		ScheduleScriptsTableSolvent scheduleTable = new ScheduleScriptsTableSolvent();
		MeteringHelper help = new MeteringHelper();
		
		help.navigateToScriptsPage();
		Assert.assertEquals(true, common.isTextPresent("MCT-400 Auto Test"));
		Assert.assertEquals(true, scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Waiting"));
		
		//Set test script start time to be one minute from the current and the stop time to be 2 min from the current
		help.setOneMinuteTestSchedule("MCT-400 Auto Test");
		String status;
		//Verify current state is pending
		if(scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Pending")) {
			Assert.assertEquals(true, scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Pending"));
			do {
				waitTenSeconds();
				status = scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test");
			}while(status.equals("Pending"));
		}
		
		//After 60 seconds verify current state is running
		if(scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Running")) {
			Assert.assertEquals(true, scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Running"));
			do {
				waitTenSeconds();
				status = scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test");
			}while(status.equals("Running"));
		}		
		
		//Wait for current state to return to waiting
		help.waitUntilNextMinute();
		waitTenSeconds();
		Assert.assertEquals(true, scheduleTable.getCurrentStateBySchedule("MCT-400 Auto Test").equals("Waiting"));
		
		//Stop test script
		common.end();
	}
}
