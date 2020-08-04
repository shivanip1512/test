package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcCreatePage;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.github.javafaker.Faker;

public class CbcCreateTests extends SeleniumTestSetup {

    private CbcCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    private Faker faker = new Faker();

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.CBC_CREATE);

        this.createPage = new CbcCreatePage(driverExt);

        randomNum = getRandomNum();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcCreate_pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create CBC";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcCreate_requiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CBC was successfully saved.";

        int masterAddress = randomNum.nextInt(65000);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT CBC " + timeStamp;
        Integer serialNumber = faker.number().numberBetween(1, 65535);
        Integer port = faker.number().numberBetween(1, 65535);
        Integer postCommWait = faker.number().numberBetween(1, 99999);
        Integer slaveAddress = faker.number().numberBetween(1,  65535);
        this.createPage.getType().selectItemByText("CBC 8020");
        this.createPage.getMasterAddress().setInputValue(String.valueOf(masterAddress));
        this.createPage.getSlaveAddress().setInputValue(slaveAddress.toString());
        this.createPage.getName().setInputValue(name);
        this.createPage.getSerialNumber().setInputValue(serialNumber.toString());
        this.createPage.getIpAddress().setInputValue(faker.internet().ipV4Address());
        this.createPage.getPort().setInputValue(port.toString());
        this.createPage.getPostCommWait().setInputValue(postCommWait.toString());

        this.createPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
