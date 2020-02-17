package com.eaton.tests.demandresponse;

import java.text.SimpleDateFormat;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private WebDriver driver;
    private Random randomNum;

    @BeforeClass
    public void beforeClass() {

        driver = getDriver();        
        
        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        this.createPage = new LoadGroupCreatePage(driver, Urls.DemandResponse.LOAD_GROUP_CREATE);
        
        randomNum = getRandomNum();
    }

    @Test(groups = {"smoketest", ""})
    public void pageTitleCorrect() {

        Assert.assertEquals(createPage.getTitle(), "Create Load Group");
    }
    
    @Test(groups = {"smoketest", "SM06_01_CreateLoadGrp()"})
    public void createEcobeeLoadGroupSuccess() {
        
        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        
        String name = "AT ecobee " + timeStamp;
        double randomDouble = randomNum.nextDouble();   
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;
        
        this.createPage.getName().setInputValue(name);
        this.createPage.getType().selectItemByText("ecobee Group");
        waitForLoadingSpinner();
        
        this.createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        
        this.createPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name);
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driver, Urls.DemandResponse.LOAD_GROUP_DETAIL);
        
        String userMsg = detailsPage.getUserMessage();
        
        Assert.assertEquals(userMsg, name + " saved successfully.");
    }    
    
    @AfterMethod
    public void afterTest() {        
        refreshPage(createPage);
    }
}
