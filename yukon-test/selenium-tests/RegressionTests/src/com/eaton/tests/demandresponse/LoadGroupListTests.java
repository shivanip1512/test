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
import com.eaton.pages.demandresponse.LoadGroupListPage;

public class LoadGroupListTests extends SeleniumTestSetup {

    private LoadGroupListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softAssertion = getSoftAssertion();        

        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUPS);

        listPage = new LoadGroupListPage(driverExt, Urls.DemandResponse.LOAD_GROUPS);
    }

    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Load Groups";
        
        String actualPageTitle = listPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 6;
        
        List<String> headers = this.listPage.getTable().getListTableHeaders();
        
        int actualCount = headers.size();
        
        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headers.contains("Name"), "Expected Column Header of Name");
        softAssertion.assertTrue(headers.contains("State"), "Expected Column Header of State");
        softAssertion.assertTrue(headers.contains("Last Action"), "Expected Column Header of Last Action");
        softAssertion.assertTrue(headers.contains("Day/Month/Season/Year Hrs"), "Expected Column Header of Day/Month/Season/Year Hrs");
        softAssertion.assertTrue(headers.contains("Reduction"), "Expected Column Header of Reduction");
    }
}
