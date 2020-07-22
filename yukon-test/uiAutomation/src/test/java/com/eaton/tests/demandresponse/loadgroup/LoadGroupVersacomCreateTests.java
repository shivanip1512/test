package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupVersacomCreatePage;

public class LoadGroupVersacomCreateTests extends SeleniumTestSetup {

    private LoadGroupVersacomCreatePage createPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
        softly = new SoftAssertions();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupVersacomCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RequiredFieldsOnlySuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Versacom " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Versacom Group");

        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AllFieldsSuccessfullyWithSerialAddress() {        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());        
        String name = "AT Versacom " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;       

        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getType().selectItemByText("Versacom Group");

        waitForLoadingSpinner();
        createPage.getCommunicationRoute().selectItemByText("a_CCU-711"); 
        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        createPage.getName().setInputValue(name);      
        createPage.getSerialAddress().setInputValue(String.valueOf("40"));
        createPage.getRelayUsage().setTrueFalseByName("Relay 2", true);
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(true);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AllFieldsSuccessfullyWithoutSerialAddress() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Versacom " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Versacom Group");

        waitForLoadingSpinner();
        createPage.getCommunicationRoute().selectItemByText("a_CCU-711");

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getAddressUsage().setTrueFalseByName("Class", true);
        createPage.getAddressUsage().setTrueFalseByName("Division", true);
        
        createPage.getUtilityAddress().setInputValue(String.valueOf(randomNum.nextInt(254)));
        createPage.getSectionAddress().setInputValue(String.valueOf(randomNum.nextInt(255)));

        createPage.getClassAddress().setTrueFalseByName("1", true);
        createPage.getDivisionAddress().setTrueFalseByName("11", true);
        
        createPage.getRelayUsage().setTrueFalseByName("Relay 3", true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Address Usage");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSectionLabelsCorrect() {
        String sectionName = "Address Usage";
        String expectedLabels = "Usage:";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
        
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Addressing");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        createPage.getType().selectItemByText("Versacom Group");

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getAddressUsage().setTrueFalseByName("Class", true);
        createPage.getAddressUsage().setTrueFalseByName("Division", true);
        createPage.getAddressUsage().setTrueFalseByName("Serial", true);

        List<String> expectedLabels = new ArrayList<>(
                List.of("Utility Address:", "Section Address:", "Class Address:", "Division Address:", "Serial Address:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RelayUsageSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Relay Usage");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RelayUsageSectionLabelsCorrect() {
        String sectionName = "Relay Usage";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String expectedLabels = "Relay Usage:";
        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_OptionalAttributesSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Optional Attributes");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_OptionalAttributesSectionLabelsCorrect() {
        String sectionName = "Optional Attributes";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Utility Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("255");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddressDefaultValueValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        assertThat(createPage.getUtilityAddress().getInputValue()).isEqualTo("1");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        createPage.getSerialAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Serial Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        createPage.getSerialAddress().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        createPage.getSerialAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getSectionAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Section Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getSectionAddress().setInputValue("257");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getSectionAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_WhenUsageSerialThenAddressUsageSectionDivisionClassDisabled() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
              
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Section")).isTrue();
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Class")).isTrue();
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Division")).isTrue();        
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_WhenUsageSectionDivisionClassAndSerialSelectedThenAddressingSectionClassDivisionDisabled() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByName("Section", true);
        createPage.getAddressUsage().setTrueFalseByName("Class", true);
        createPage.getAddressUsage().setTrueFalseByName("Division", true);
        createPage.getAddressUsage().setTrueFalseByName("Serial", true);

        assertThat(createPage.getClassAddress().allValuesDisabled()).isTrue();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupVersacomCreatePage(driverExt);
    }
}
