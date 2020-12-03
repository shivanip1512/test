package com.eaton.tests.capcontrol;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.CbcCreatePage;
import com.eaton.pages.capcontrol.CbcDetailPage;
import com.github.javafaker.Faker;

public class CbcCreateTests extends SeleniumTestSetup {

    private CbcCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker = new Faker();

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        navigate(Urls.CapControl.CBC_CREATE);

        createPage = new CbcCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void cbcCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create CBC";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void cbcCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "CBC was successfully saved.";

        int masterAddress = faker.number().numberBetween(1, 65000);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT CBC " + timeStamp;
        Integer serialNumber = faker.number().numberBetween(1, 65535);
        Integer port = faker.number().numberBetween(1, 65535);
        Integer postCommWait = faker.number().numberBetween(1, 99999);
        Integer slaveAddress = faker.number().numberBetween(1,  65535);
        createPage.getType().selectItemByValue("CBC_8020");
        createPage.getName().setInputValue(name);
        createPage.getSerialNumber().setInputValue(serialNumber.toString());
        createPage.getMasterAddress().setInputValue(String.valueOf(masterAddress));
        createPage.getSlaveAddress().setInputValue(slaveAddress.toString());
        String commChannelId = TestDbDataType.CommChannelData.A_IPC410FL_ID.getId().toString();
        createPage.getCommChannel().selectItemByValue(commChannelId);
        createPage.getIpAddress().setInputValue(faker.internet().ipV4Address());
        createPage.getPort().setInputValue(port.toString());
        createPage.getPostCommWait().setInputValue(postCommWait.toString());

        createPage.getSaveBtn().click();

        waitForPageToLoad("CBC: " + name, Optional.empty());

        CbcDetailPage detailPage = new CbcDetailPage(driverExt);

        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
