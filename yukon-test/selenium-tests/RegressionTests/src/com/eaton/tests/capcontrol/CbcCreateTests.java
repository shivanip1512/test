package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcCreatePage;
import com.eaton.pages.capcontrol.CbcDetailPage;

public class CbcCreateTests extends SeleniumTestSetup {

    private CbcCreatePage createPage;
    private WebDriver driver;
    Random randomNum;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();

        driver.get(getBaseUrl() + Urls.CapControl.CBC_CREATE);

        this.createPage = new CbcCreatePage(driver, Urls.CapControl.CBC_CREATE);

        randomNum = new Random();
    }

    @Test(groups = { "smoketest", "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create CBC");
    }

    @Test(enabled = false, groups = { "smoketest", "SM03_03_CreateCCObjects" })
    public void createCbcRequiredFieldsOnlySuccess() {

        int masterAddress = randomNum.nextInt(65000);

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT CBC " + timeStamp;
        this.createPage.getType().selectItemByText("CBC 8020");
        this.createPage.getMasterAddress().setInputValue(String.valueOf(masterAddress));
        this.createPage.getName().setInputValue(name);

        this.createPage.getSaveBtn().click();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        waitForPageToLoad("CBC: " + name);

        CbcDetailPage detailPage = new CbcDetailPage(driver, Urls.CapControl.CBC_DETAIL);

        String userMsg = detailPage.getUserMessageSuccess();

        Assert.assertEquals(userMsg, "CBC was saved successfully.");
    }

    @AfterMethod
    public void afterTest() {
        refreshPage(createPage);
    }
}
