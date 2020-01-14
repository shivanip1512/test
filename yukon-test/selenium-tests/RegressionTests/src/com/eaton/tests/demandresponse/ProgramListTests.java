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
import com.eaton.pages.demandresponse.ProgramListPage;

public class ProgramListTests extends SeleniumTestSetup {

    private ProgramListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.PROGRAMS);

        listPage = new ProgramListPage(driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(listPage.getTitle(), "Programs");
    }

    @Test
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 9);
        softAssertion.assertTrue(headerList.contains("Name"));
        softAssertion.assertTrue(headerList.contains("State"));
        softAssertion.assertTrue(headerList.contains("Start"));
        softAssertion.assertTrue(headerList.contains("Stop"));
        softAssertion.assertTrue(headerList.contains("Current Gear"));
        softAssertion.assertTrue(headerList.contains("Priority"));
        softAssertion.assertTrue(headerList.contains("Reduction"));
        softAssertion.assertTrue(headerList.contains("kW Savings (Max/Now)"));
    }
}
