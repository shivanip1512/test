package com.eaton.tests.capcontrol.orphans;

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
import com.eaton.pages.capcontrol.orphans.CbcListPage;

public class CbcListTests extends SeleniumTestSetup {

    CbcListPage listPage;
    WebDriver driver;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();
        this.softAssertion = getSoftAssertion();

        this.driver.get(getBaseUrl() + Urls.CapControl.CBC_LIST);

        this.listPage = new CbcListPage(this.driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 3);
        this.softAssertion.assertTrue(headerList.contains("Name"));
        this.softAssertion.assertTrue(headerList.contains("Item Type"));
        this.softAssertion.assertTrue(headerList.contains("Description"));
    }
}
