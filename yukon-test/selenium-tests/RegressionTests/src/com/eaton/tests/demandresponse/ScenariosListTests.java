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
import com.eaton.pages.demandresponse.ScenariosListPage;

public class ScenariosListTests extends SeleniumTestSetup {

    DriverExtensions driverExt;
    ScenariosListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.SCENARIOS);

        this.listPage = new ScenariosListPage(driverExt, Urls.DemandResponse.SCENARIOS);
    }

    @Test
    public void titleCorrect() {
        final String EXPECTED_TITLE = "Scenarios";
        
        String actualPageTitle = listPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 2;
        
        List<WebTableColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        int actualCount = headerList.size();
        
        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headerList.contains("Name"), "Expected Column Header of Name");
    }

    @Test(enabled = false)
    public void startScenarioModalTitleCorrect() {
        // TODO get modal to be selected uniquely
    }

    @Test(enabled = false)
    public void stopScenarioModalTitleCorrect() {
        // TODO get modal to be selected uniquely
    }
}
