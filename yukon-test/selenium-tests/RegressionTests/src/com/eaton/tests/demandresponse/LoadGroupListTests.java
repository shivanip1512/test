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
import com.eaton.pages.demandresponse.LoadGroupListPage;

public class LoadGroupListTests extends SeleniumTestSetup {

    WebDriver driver;
    LoadGroupListPage listPage;
    SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        this.driver = getDriver();
        this.softAssertion = getSoftAssertion();

        this.driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUPS);

        this.listPage = new LoadGroupListPage(this.driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.listPage.getTitle(), "Load Groups");
    }

    @Test
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = this.listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 6);
        this.softAssertion.assertTrue(headerList.contains("Name"));
        this.softAssertion.assertTrue(headerList.contains("State"));
        this.softAssertion.assertTrue(headerList.contains("Last Action"));
        this.softAssertion.assertTrue(headerList.contains("Day/Month/Season/Year Hrs"));
        this.softAssertion.assertTrue(headerList.contains("Reduction"));
    }
}
