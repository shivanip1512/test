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
import com.eaton.pages.capcontrol.orphans.RegulatorListPage;

public class RegulatorListTests extends SeleniumTestSetup {

    private RegulatorListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.CapControl.REGULATOR_LIST);

        listPage = new RegulatorListPage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = listPage.getTable().getColumnHeaders();

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