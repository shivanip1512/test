package com.eaton.tests.support;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SupportBundlePage;
import com.eaton.pages.support.SupportPage;

public class SupportDetailsCreateSupportBundleTests extends SeleniumTestSetup {

	private SoftAssertions softly;
	private DriverExtensions driverExt;
    private SupportPage supportPage;
    private SupportBundlePage supportBundlePage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.SUPPORT);
        softly = new SoftAssertions();
        
        supportPage = new SupportPage(driverExt);
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
    	refreshPage(supportPage);
    	supportPage = new SupportPage(driverExt);
    }
    
    //================================================================================
    // Support Bundle
    //================================================================================
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleRequiredFieldsOnlySuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleRangeTwoWeeksSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getRange().selectItemByIndex(SupportPage.RANGE_LAST_TWO_WEEKS_INDEX);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleRangeMonthSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getRange().selectItemByIndex(SupportPage.RANGE_LAST_MONTH_INDEX);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleRangeEverythingSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getRange().selectItemByIndex(SupportPage.RANGE_EVERYTHING_INDEX);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleCommLogSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getCommLogFiles().setValue(true);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleNoteSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
        int noteLength = new Random().nextInt(1024);
        String note = RandomStringUtils.randomAlphabetic(noteLength);
        
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getNotes().setInputValue(note);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Admin.ADMIN })
    public void createSupportBundleEverythingSuccess() {
    	final String EXPECTED_PENDING_STATUS = "Pending";
    	final String EXPECTED_STATUS = "Finished";
    	String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;
        
        int noteLength = new Random().nextInt(1024);
        String note = RandomStringUtils.randomAlphabetic(noteLength);
        
        
    	supportPage.getCustomerName().setInputValue(name);
    	supportPage.getRange().selectItemByIndex(SupportPage.RANGE_EVERYTHING_INDEX);
    	supportPage.getCommLogFiles().setValue(true);
    	supportPage.getNotes().setInputValue(note);
    	supportPage.getCreateBundleBtn().click();
    	
    	waitForPageToLoad(SupportBundlePage.PAGE_TITLE,Optional.empty());
    	
    	supportBundlePage = new SupportBundlePage(driverExt);
    	softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
    	softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
    	softly.assertAll();
    }
}
