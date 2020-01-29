package com.eaton.tests.capcontrol;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.elements.WebTableColumnHeader;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.SubstationBusListPage;

public class SubstationBusListTests extends SeleniumTestSetup {

    private SubstationBusListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.CapControl.SUBSTATION_BUS_LIST);

        listPage = new SubstationBusListPage(driver, null);
    }

    @Test(groups = { "smoketest", "SmokeTest_CapControl" })
    public void columnHeadersCorrect() {

        List<WebTableColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebTableColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 3);
        softAssertion.assertTrue(headerList.contains("Name"));
        softAssertion.assertTrue(headerList.contains("Item Type"));
        softAssertion.assertTrue(headerList.contains("Description"));
    }
}
