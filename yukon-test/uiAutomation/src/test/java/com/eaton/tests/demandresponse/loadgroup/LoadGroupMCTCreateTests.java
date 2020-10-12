package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupMCTCreatePage;
import com.github.javafaker.Faker;

public class LoadGroupMCTCreateTests extends SeleniumTestSetup {

    private LoadGroupMCTCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupMCTCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_RequiredFieldsWithBronzeAddress_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue("2");
        createPage.getRelayUsage().setTrueFalseByLabel("Relay 2", "RELAY_2", true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getDisableGroup().selectValue("Yes");
        createPage.getDisableControl().selectValue("No");
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_CommunicationRoute_LabelsCorrect() {
        String sectionName = "General";
        String expectedLabel = "Communication Route:";

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels.contains(expectedLabel)).isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_AddressingSection_LabelsCorrect() {
        String sectionName = "Addressing";
        List<String> expectedLabels = new ArrayList<>(List.of("Address Level:", "Address:", "Relay Usage:"));

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsAll(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_AddressingSection_TitleCorrect() {

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        Section address = createPage.getPageSection("Addressing");
        assertThat(address.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_Address_MinRangeValidation() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        String expectedErrorMsg = "Must be between 1 and 2,147,483,647.";

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue("0");

        createPage.getSaveBtn().click();
        assertThat(createPage.getAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    }

    @Test(enabled = false, groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_Address_MaxRangeValidation() {
        throw new SkipException("Development Defect: YUK-22593");
//        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
//        String name = "AT MCT " + timeStamp;
//        String expectedErrorMsg = "Must be between 1 and 2,147,483,647.";
//
//        createPage.getType().selectItemByValue("LM_GROUP_MCT");
//        waitForLoadingSpinner();
//
//        createPage.getName().setInputValue(name);
//        createPage.getAddress().setInputValue("2147483648");
//
//        createPage.getSaveBtn().click();
//        assertThat(createPage.getAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_Address_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();
        createPage.getAddress().clearInputValue();
        createPage.getSaveBtn().click();

        assertThat(createPage.getAddress().getValidationError()).isEqualTo("Address is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_MCTAddressLabel_DefaultValueCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        createPage.getAddressLevel().selectItemByValue("MCT_ADDRESS");

        assertThat(createPage.getMctAddress().getLinkValue()).isEqualTo("(none selected)");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_MCTAddress_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        createPage.getAddressLevel().selectItemByValue("MCT_ADDRESS");
        createPage.getSaveBtn().click();

        assertThat(createPage.getMctAddressValidationMsg()).isEqualTo("MCT Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_MCTAddress_ValueSelectedCorrect() {
        setRefreshPage(true);
        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        createPage.getAddressLevel().selectItemByValue("MCT_ADDRESS");

        SelectMCTMeterModal mctMeterModal = this.createPage.showAndWaitMCTMeter();
        mctMeterModal.selectMeter("a_MCT-430A");
        mctMeterModal.clickOkAndWaitForModalToClose();

        assertThat(createPage.getMctAddress().getLinkValue()).contains("a_MCT-430A");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateMct_RequiredFieldsWithMctAddress_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue("LM_GROUP_MCT");
        waitForLoadingSpinner();

        createPage.getName().setInputValue(name);
        createPage.getAddressLevel().selectItemByValue("MCT_ADDRESS");

        SelectMCTMeterModal mctMeterModal = createPage.showAndWaitMCTMeter();
        mctMeterModal.selectMeter("a_MCT-430A");
        mctMeterModal.clickOkAndWaitForModalToClose();
        createPage.getSaveBtn().click();
        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
