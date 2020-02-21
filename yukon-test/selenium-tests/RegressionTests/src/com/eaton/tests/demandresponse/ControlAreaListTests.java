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
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaListPage;

@Test(groups = TestNgGroupConstants.DEMAND_REPONSE)
public class ControlAreaListTests extends SeleniumTestSetup {

    private ControlAreaListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.CONTROL_AREA);

        listPage = new ControlAreaListPage(driverExt, null);
    }
    
    @Test
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Control Areas";
        
        String actualPageTitle = listPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 8;
        
        List<WebTableColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }
        
        int actualCount = headerList.size();
        
        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headerList.contains("Name"), "Expected Column Header of Name");
        softAssertion.assertTrue(headerList.contains("State"), "Expected Column Header of State");
        softAssertion.assertTrue(headerList.contains("Value / Threshold"), "Expected Column Header of Value / Threshold");
        softAssertion.assertTrue(headerList.contains("Peak / Projection"), "Expected Column Header of Peak / Projection");
        softAssertion.assertTrue(headerList.contains("ATKU"), "Expected Column Header of ATKU");
        softAssertion.assertTrue(headerList.contains("Priority"), "Expected Column Header of Priority");
        softAssertion.assertTrue(headerList.contains("Time Window"), "Expected Column Header of Time Window");
    }
}
