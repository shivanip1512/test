package com.eaton.tests.demandresponse;

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
import com.eaton.framework.TestNgGroupConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        createPage = new LoadGroupCreatePage(driverExt, Urls.DemandResponse.LOAD_GROUP_CREATE);
        
        randomNum = getRandomNum();
    }

    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_01_CreateLoadGrp()"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";
        
        String actualPageTitle = createPage.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    }
    
    @Test(groups = {TestNgGroupConstants.SMOKE_TESTS, "SM06_01_CreateLoadGrp()"})
    public void createEcobeeLoadGroupSuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT ecobee " + timeStamp;
        double randomDouble = randomNum.nextDouble();   
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;
        
        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("ecobee Group");
        waitForLoadingSpinner();
        
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, Urls.DemandResponse.LOAD_GROUP_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.", "Expected User Msg: '" + name + " saved successfully.' but found: " + userMsg);
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}
