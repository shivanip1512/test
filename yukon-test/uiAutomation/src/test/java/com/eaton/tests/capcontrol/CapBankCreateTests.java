package com.eaton.tests.capcontrol;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CapBankCreatePage;
import com.eaton.pages.capcontrol.CapBankDetailPage;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.openqa.selenium.WebDriver;

public class CapBankCreateTests extends SeleniumTestSetup {

    private CapBankCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.CAP_BANK_CREATE);

        this.createPage = new CapBankCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void capBankCreate_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create CapBank";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);

    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void capBankCreate_requiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CapBank was saved successfully.";

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT CapBank " + timeStamp;
        this.createPage.getName().setInputValue(name);

        this.createPage.getSaveBtn().click();

        waitForPageToLoad("CapBank: " + name, Optional.empty());

        CapBankDetailPage detailsPage = new CapBankDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
