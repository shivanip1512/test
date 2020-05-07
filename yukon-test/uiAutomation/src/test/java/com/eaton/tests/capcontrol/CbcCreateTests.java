package com.eaton.tests.capcontrol;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcCreatePage;
import com.eaton.pages.capcontrol.CbcDetailPage;

public class CbcCreateTests extends SeleniumTestSetup {

    private CbcCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();

        driver.get(getBaseUrl() + Urls.CapControl.CBC_CREATE);

        this.createPage = new CbcCreatePage(driverExt);

        randomNum = getRandomNum();
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create CBC";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_03_CreateCCObjects" })
    public void createCbcRequiredFieldsOnlySuccess() {
        final String EXPECTED_MSG = "CBC was successfully saved.";
        
        int masterAddress = randomNum.nextInt(65000);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        String name = "AT CBC " + timeStamp;
        this.createPage.getType().selectItemByText("CBC 8020");
        this.createPage.getMasterAddress().setInputValue(String.valueOf(masterAddress));
        this.createPage.getName().setInputValue(name);

        this.createPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        Assert.assertEquals(userMsg, EXPECTED_MSG, "Expected User MsgL:'" + EXPECTED_MSG + "' but found: " + userMsg);
    }

    @AfterMethod(alwaysRun=true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
