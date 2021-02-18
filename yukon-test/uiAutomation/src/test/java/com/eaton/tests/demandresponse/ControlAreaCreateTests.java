package com.eaton.tests.demandresponse;

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
import com.eaton.pages.demandresponse.ControlAreaCreatePage;
import com.eaton.pages.demandresponse.ControlAreaDetailPage;

public class ControlAreaCreateTests extends SeleniumTestSetup {

    private ControlAreaCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        navigate(Urls.DemandResponse.CONTROL_AREA_CREATE);
        createPage = new ControlAreaCreatePage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaCreate_Page_TitleCorrect() {
        navigate(Urls.DemandResponse.CONTROL_AREA_CREATE);
        final String EXPECTED_TITLE = "Create Control Area";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void controlAreaCreate_RequiredFieldsOnly_Success() {
        setRefreshPage(true);

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        String name = "AT Control Area " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);

        createPage.getProgramAssignments().addSingleAvailable("AT Direct Program for Create Control Area");

        createPage.getSave().click();

        waitForPageToLoad("Control Area: " + name, Optional.empty());

        ControlAreaDetailPage detailsPage = new ControlAreaDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
