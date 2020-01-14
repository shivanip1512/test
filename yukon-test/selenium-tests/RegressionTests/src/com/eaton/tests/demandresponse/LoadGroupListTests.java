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

    private LoadGroupListPage listPage;
    private SoftAssert softAssertion;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        softAssertion = getSoftAssertion();

        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUPS);

        listPage = new LoadGroupListPage(driver, null);
    }

    @Test
    public void titleCorrect() {

        Assert.assertEquals(this.listPage.getTitle(), "Load Groups");
    }

    @Test
    public void columnHeadersCorrect() {

        List<ColumnHeader> headers = listPage.getTable().getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (ColumnHeader header : headers) {
            headerList.add(header.getColumnName());
        }

        Assert.assertEquals(headerList.size(), 6);
        softAssertion.assertTrue(headerList.contains("Name"));
        softAssertion.assertTrue(headerList.contains("State"));
        softAssertion.assertTrue(headerList.contains("Last Action"));
        softAssertion.assertTrue(headerList.contains("Day/Month/Season/Year Hrs"));
        softAssertion.assertTrue(headerList.contains("Reduction"));
    }
}
