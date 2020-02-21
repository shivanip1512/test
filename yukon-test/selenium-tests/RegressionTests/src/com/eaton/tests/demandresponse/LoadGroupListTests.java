package com.eaton.tests.demandresponse;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.elements.WebTableColumnHeader;
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
        
        List<WebTableColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }
        
        int actualCount = headerList.size();
        
        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headerList.contains("Name"), "Expected Column Header of Name");
        softAssertion.assertTrue(headerList.contains("State"), "Expected Column Header of State");
        softAssertion.assertTrue(headerList.contains("Last Action"), "Expected Column Header of Last Action");
        softAssertion.assertTrue(headerList.contains("Day/Month/Season/Year Hrs"), "Expected Column Header of Day/Month/Season/Year Hrs");
        softAssertion.assertTrue(headerList.contains("Reduction"), "Expected Column Header of Reduction");
    }
}
