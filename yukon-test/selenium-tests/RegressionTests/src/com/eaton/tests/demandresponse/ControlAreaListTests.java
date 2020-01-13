package com.eaton.tests.demandresponse;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.elements.ColumnHeader;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.ControlAreaListPage;

public class ControlAreaListTests extends SeleniumTestSetup {

    WebDriver driver;
    ControlAreaListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();
        this.softAssertion = getSoftAssertion();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.CONTROL_AREA);

        this.listPage = new ControlAreaListPage(this.driver, null);
    }

    @Test
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 9);
        this.softAssertion.assertTrue(headerList.contains("Name"));
        this.softAssertion.assertTrue(headerList.contains("State"));
        this.softAssertion.assertTrue(headerList.contains("Value / Threshold"));
        this.softAssertion.assertTrue(headerList.contains("Peak / Projection"));
        this.softAssertion.assertTrue(headerList.contains("ATKU"));
        this.softAssertion.assertTrue(headerList.contains("Priority"));
        this.softAssertion.assertTrue(headerList.contains("Time Window"));
        this.softAssertion.assertTrue(headerList.contains("kW Savings (Max/Now)"));
    }

    @Test(groups = { "smoketest", "SmokeTest_DemandResponse" })
    public void pageTitleCorrect() {
        Assert.assertEquals(this.listPage.getTitle(), "Control Areas");
    }
}
