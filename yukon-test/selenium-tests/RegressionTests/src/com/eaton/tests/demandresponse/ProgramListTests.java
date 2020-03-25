package com.eaton.tests.demandresponse;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ProgramListPage;

public class ProgramListTests extends SeleniumTestSetup {

    private ProgramListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.PROGRAMS);

        listPage = new ProgramListPage(driverExt);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Programs";
        
        String actualPageTitle = listPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page Title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 8;

        List<String> headers = this.listPage.getTable().getListTableHeaders();
        
        int actualCount = headers.size();

        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headers.contains("Name"), "Expected Column Header of Name");
        softAssertion.assertTrue(headers.contains("State"), "Expected Column Header of State");
        softAssertion.assertTrue(headers.contains("Start"), "Expected Column Header of Start");
        softAssertion.assertTrue(headers.contains("Stop"), "Expected Column Header of Stop");
        softAssertion.assertTrue(headers.contains("Current Gear"), "Expected Column Header of Current Gear");
        softAssertion.assertTrue(headers.contains("Priority"), "Expected Column Header of Priority");
        softAssertion.assertTrue(headers.contains("Reduction"), "Expected Column Header of Reduction");
    }
}
