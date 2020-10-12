package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDigiSepCreatePage;
import com.github.javafaker.Faker;

public class LoadGroupDigiSepCreateTests extends SeleniumTestSetup {

    private LoadGroupDigiSepCreatePage createPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupDigiSepCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_AllFields_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT DigiSep " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        createPage.getDeviceClass().selectItemByText("Generation Systems");
        createPage.getDeviceClass().selectItemByText("Smart Appliances");
        createPage.getUtilityEnrollmentGroup().setInputValue(String.valueOf("100"));
        createPage.getRampInTime().setInputValue(String.valueOf("500"));
        createPage.getRampOutTime().setInputValue(String.valueOf("999"));

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().selectValue("Yes");
        createPage.getDisableControl().selectValue("No");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClass_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        
        createPage.getSaveBtn().click();

        assertThat(createPage.getDeviceClass().getValidationError()).isEqualTo("At least one device class set must be assigned.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroup_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getUtilityEnrollmentGroup().clearInputValue();
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError()).isEqualTo("Utility Enrollment Group is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroup_MinValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getUtilityEnrollmentGroup().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError()).isEqualTo("Must be between 1 and 255.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroup_MaxValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getUtilityEnrollmentGroup().setInputValue("256");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError()).isEqualTo("Must be between 1 and 255.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTime_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampInTime().clearInputValue();
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Ramp In Time is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTime_MinValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampInTime().setInputValue("-9999999");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTime_MaxValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampInTime().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTime_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampOutTime().clearInputValue();
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Ramp Out Time is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTime_MinValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampOutTime().setInputValue("-9999999");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTime_MaxValueValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();

        createPage.getRampOutTime().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_GeneralSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_GeneralSection_LabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClassSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Device Class");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClassSection_LabelsCorrect() {
        String sectionName = "Device Class";
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Device Class:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_EnrollmentSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Enrollment");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_EnrollmentSection_LabelsCorrect() {
        String sectionName = "Enrollment";
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Utility Enrollment Group:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_TimingSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Timing");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_TimingSection_LabelsCorrect() {
        String sectionName = "Timing";
        createPage.getType().selectItemByValue("LM_GROUP_DIGI_SEP");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Ramp In Time:", "Ramp Out Time:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }
}
