package com.eaton.tests.demandresponse.loadgroup;

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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.github.javafaker.Faker;

public class LoadGroupEcobeeCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
        faker = SeleniumTestSetup.getFaker();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateEcobee_AllFieldsDisableFalse_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Ecobee " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_GROUP_ECOBEE");
        waitForLoadingSpinner();

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateEcobee_AllFieldsDisableTrue_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Ecobee " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_GROUP_ECOBEE");
        waitForLoadingSpinner();

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getDisableGroup().selectValue("Yes");
        createPage.getDisableControl().selectValue("Yes");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }
}
