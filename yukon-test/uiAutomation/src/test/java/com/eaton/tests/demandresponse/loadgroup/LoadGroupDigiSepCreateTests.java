package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupDigiSepCreatePage;

public class LoadGroupDigiSepCreateTests extends SeleniumTestSetup {

    private LoadGroupDigiSepCreatePage createPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupDigiSepCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_AllFieldsSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT DigiSep " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByText("Digi SEP Group");

        waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        createPage.getDeviceClass().selectItemByText("Generation Systems");
        createPage.getUtilityEnrollmentGroup().setInputValue(String.valueOf("100"));
        createPage.getRampInTime().setInputValue(String.valueOf("500"));
        createPage.getRampOutTime().setInputValue(String.valueOf("999"));

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(false);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClassBlankValue() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getSaveBtn().click();

        assertThat(createPage.getDeviceClass().getValidationError()).isEqualTo("At least one device class set must be assigned.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroupBlankValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError())
                .isEqualTo("Utility Enrollment Group is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroupMinValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getUtilityEnrollmentGroup().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError()).isEqualTo("Must be between 1 and 255.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_UtilityEnrollmentGroupMaxValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getUtilityEnrollmentGroup().setInputValue("256");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityEnrollmentGroup().getValidationError()).isEqualTo("Must be between 1 and 255.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTimeBlankValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampInTime().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Ramp In Time is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTimeMinValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampInTime().setInputValue("-9999999");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampInTimeMaxValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampInTime().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampInTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTimeBlankValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampOutTime().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Ramp Out Time is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTimeMinValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampOutTime().setInputValue("-9999999");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_RampOutTimeMaxValueValidation() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();

        createPage.getRampOutTime().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getRampOutTime().getValidationError()).isEqualTo("Must be between -99,999 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClassSectionTitleCorrect() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Device Class");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_DeviceClassSectionLabelsCorrect() {
        String sectionName = "Device Class";
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Device Class:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_EnrollmentSectionTitleCorrect() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Enrollment");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_EnrollmentSectionLabelsCorrect() {
        String sectionName = "Enrollment";
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Utility Enrollment Group:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_TimingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Timing");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateDigiSEP_TimingSectionLabelsCorrect() {
        String sectionName = "Timing";
        createPage.getType().selectItemByText("Digi SEP Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Ramp In Time:", "Ramp Out Time:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}
