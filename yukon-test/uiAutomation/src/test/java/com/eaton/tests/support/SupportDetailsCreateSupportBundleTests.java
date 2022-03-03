package com.eaton.tests.support;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.support.SupportBundlePage;
import com.eaton.pages.support.SupportPage;
import com.github.javafaker.Faker;

public class SupportDetailsCreateSupportBundleTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private SupportPage supportPage;
    private SupportBundlePage supportBundlePage;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
        navigate(Urls.SUPPORT);
        supportPage = new SupportPage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(supportPage);
    }

    // ================================================================================
    // Support Bundle
    // ================================================================================

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_RequiredFieldsOnly_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_RangeTwoWeeks_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getRange().selectItemByIndex(SupportPage.RANGE_LAST_TWO_WEEKS_INDEX);
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_RangeMonth_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getRange().selectItemByIndex(SupportPage.RANGE_LAST_MONTH_INDEX);
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_RangeEverything_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getRange().selectItemByIndex(SupportPage.RANGE_EVERYTHING_INDEX);
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_CommLog_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getCommLogFiles().setValue(true);        
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ADMIN })
    public void createSupportBundle_AllFields_Success() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_PENDING_STATUS = "Pending";
        final String EXPECTED_STATUS = "Finished";
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "Test " + timeStamp;

        String note = faker.lorem().paragraph();

        supportPage.getCustomerName().setInputValue(name);
        supportPage.getRange().selectItemByIndex(SupportPage.RANGE_EVERYTHING_INDEX);
        supportPage.getCommLogFiles().setValue(true);
        supportPage.getNotes().setInputValue(note);
        supportPage.getCreateBundleBtn().click();

        waitForPageToLoad(SupportBundlePage.PAGE_TITLE, Optional.empty());

        supportBundlePage = new SupportBundlePage(driverExt);
        softly.assertThat(supportBundlePage.pollSupportBundleItemsStatusNot(EXPECTED_PENDING_STATUS)).isEqualTo(true);
        softly.assertThat(supportBundlePage.pollSupportBundleStatus(EXPECTED_STATUS)).isEqualTo(true);
        softly.assertAll();
    }
}
