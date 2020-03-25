package com.eaton.tests.capcontrol;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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

        this.listPage = new FeederListPage(driverExt);
    }

    @Test
    public void columnHeadersCorrect() {
        final int EXPECTED_COUNT = 3;

        List<String> headers = this.listPage.getTable().getListTableHeaders();
        
        int actualCount = headers.size();

        softAssertion.assertEquals(actualCount, EXPECTED_COUNT, "Expected: " + EXPECTED_COUNT + "colmns but found: " + actualCount);
        softAssertion.assertTrue(headers.contains("Name"), "Expected Column Header of Name");
        softAssertion.assertTrue(headers.contains("Item Type"), "Expected Column Header of Item Type");
        softAssertion.assertTrue(headers.contains("Description"), "Expected Column Header of Description");
    }
}
