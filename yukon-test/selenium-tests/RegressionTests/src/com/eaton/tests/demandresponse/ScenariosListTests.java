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
import com.eaton.pages.demandresponse.ScenariosListPage;

public class ScenariosListTests extends SeleniumTestSetup {

    WebDriver driver;
    ScenariosListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();
        this.softAssertion = getSoftAssertion();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.SCENARIOS);

        this.listPage = new ScenariosListPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_DemandResponse" })
    public void titleCorrect() {
        Assert.assertEquals(this.listPage.getTitle(), "Scenarios");
    }

    @Test
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 3);
        this.softAssertion.assertTrue(headerList.contains("Name"));
        this.softAssertion.assertTrue(headerList.contains("kW Savings (Max/Now)"));
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
