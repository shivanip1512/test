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

    WebDriver driver;
    ProgramListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();
        this.softAssertion = getSoftAssertion();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.PROGRAMS);

        this.listPage = new ProgramListPage(this.driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.listPage.getTitle(), "Programs");
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
        this.softAssertion.assertTrue(headerList.contains("Start"));
        this.softAssertion.assertTrue(headerList.contains("Stop"));
        this.softAssertion.assertTrue(headerList.contains("Current Gear"));
        this.softAssertion.assertTrue(headerList.contains("Priority"));
        this.softAssertion.assertTrue(headerList.contains("Reduction"));
        this.softAssertion.assertTrue(headerList.contains("kW Savings (Max/Now)"));
    }
}
