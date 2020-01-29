package com.eaton.tests.demandresponse;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.elements.WebTableColumnHeader;
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
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.CONTROL_AREA);

        listPage = new ControlAreaListPage(driver, null);
    }

    @Test
    public void columnHeadersCorrect() {

        List<WebTableColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 8);
        softAssertion.assertTrue(headerList.contains("Name"));
        softAssertion.assertTrue(headerList.contains("State"));
        softAssertion.assertTrue(headerList.contains("Value / Threshold"));
        softAssertion.assertTrue(headerList.contains("Peak / Projection"));
        softAssertion.assertTrue(headerList.contains("ATKU"));
        softAssertion.assertTrue(headerList.contains("Priority"));
        softAssertion.assertTrue(headerList.contains("Time Window"));
    }

    @Test(groups = { TestNgGroupConstants.SMOKE_TESTS })
    public void pageTitleCorrect() {
        Assert.assertEquals(listPage.getTitle(), "Control Areas");
    }
}
