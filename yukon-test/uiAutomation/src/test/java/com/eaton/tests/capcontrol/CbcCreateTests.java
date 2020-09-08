package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

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
        driverExt = getDriverExt();

        navigate(Urls.CapControl.CBC_CREATE);

        createPage = new CbcCreatePage(driverExt);

        randomNum = getRandomNum();
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcCreate_pageTitle_Correct() {
        final String EXPECTED_TITLE = "Create CBC";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.VoltVar.VOLT_VAR })
    public void cbcCreate_requiredFieldsOnly_Success() {
        final String EXPECTED_MSG = "CBC was successfully saved.";

        int masterAddress = randomNum.nextInt(65000);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT CBC " + timeStamp;
        Integer serialNumber = faker.number().numberBetween(1, 65535);
        Integer postCommWait = faker.number().numberBetween(1, 99999);
        Integer slaveAddress = faker.number().numberBetween(1,  65535);
        createPage.getType().selectItemByValue("CBC_8020");
        createPage.getName().setInputValue(name);
        createPage.getSerialNumber().setInputValue(serialNumber.toString());
        createPage.getMasterAddress().setInputValue(String.valueOf(masterAddress));
        createPage.getSlaveAddress().setInputValue(slaveAddress.toString());        
        createPage.getPostCommWait().setInputValue(postCommWait.toString());

        createPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
