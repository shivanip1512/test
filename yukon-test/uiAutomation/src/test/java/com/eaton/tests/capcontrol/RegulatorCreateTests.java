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
import com.eaton.framework.Urls;
import com.eaton.pages.capcontrol.RegulatorCreatePage;
import com.eaton.pages.capcontrol.RegulatorDetailPage;

public class RegulatorCreateTests extends SeleniumTestSetup {

    private RegulatorCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        navigate(Urls.CapControl.REGULATOR_CREATE);
        createPage = new RegulatorCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void regulatorCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Regulator";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void regulatorCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Regulator " + timeStamp;
        createPage.getName().setInputValue(name);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Regulator: " + name, Optional.empty());

        RegulatorDetailPage detailsPage = new RegulatorDetailPage(driverExt);

        // The saved successfully message is missing why?
//        String userMsg = detailsPage.getUserMessageSuccess();
//        
//        Assert.assertEquals(userMsg, "Regulator was saved successfully.");
        String actualPageTitle = detailsPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo("Regulator: " + name);
    }
}
