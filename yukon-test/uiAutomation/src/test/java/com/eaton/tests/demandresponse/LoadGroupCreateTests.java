package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        createPage = new LoadGroupCreatePage(driverExt);
        
        randomNum = getRandomNum();
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_01_CreateLoadGrp()"})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, "SM06_01_CreateLoadGrp()"})
    public void createEcobeeLoadGroupSuccess() {
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT ecobee " + timeStamp;
        double randomDouble = randomNum.nextDouble();   
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("ecobee Group");
        waitForLoadingSpinner();
        
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }    
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}
