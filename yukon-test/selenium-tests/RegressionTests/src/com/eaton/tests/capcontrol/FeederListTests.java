package com.eaton.tests.capcontrol;

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
import com.eaton.pages.capcontrol.FeederListPage;

public class FeederListTests extends SeleniumTestSetup {

    FeederListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        DriverExtensions driverExt = getDriverExt();
        this.softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.CapControl.FEEDER_LIST);

        this.listPage = new FeederListPage(driverExt, Urls.CapControl.FEEDER_LIST);
    }

    @Test
    public void columnHeadersCorrect() {

        List<WebTableColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 3);
        this.softAssertion.assertTrue(headerList.contains("Name"));
        this.softAssertion.assertTrue(headerList.contains("Item Type"));
        this.softAssertion.assertTrue(headerList.contains("Description"));
    }
}
