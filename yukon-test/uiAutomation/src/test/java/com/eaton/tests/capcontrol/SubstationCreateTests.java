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
import com.eaton.pages.capcontrol.SubstationCreatePage;
import com.eaton.pages.capcontrol.SubstationDetailPage;

public class SubstationCreateTests extends SeleniumTestSetup {

    private SubstationCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        navigate(Urls.CapControl.SUBSTATION_CREATE);
        createPage = new SubstationCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void substationCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Substation";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VOLT_VAR })
    public void substationCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Substation was saved successfully.";

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Substation " + timeStamp;
        createPage.getName().setInputValue(name);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Substation: " + name, Optional.empty());

        SubstationDetailPage detailsPage = new SubstationDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
