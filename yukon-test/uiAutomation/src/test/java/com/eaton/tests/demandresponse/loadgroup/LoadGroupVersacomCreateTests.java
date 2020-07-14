package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.MultiSelectCheckboxElement;
import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupVersacomCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        driver = getDriver();
        driverExt = getDriverExt();
        randomNum = getRandomNum();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_MandatoryFieldsSuccessfully() {
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
        createPage.getAddressToUse().getValues().get(0);
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_AllFieldsSuccessfullyWithSerialAddress() {
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

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }

        createPage.getSerialAddress().setInputValue(String.valueOf("40"));

        String[] relayUsage = { "Relay 2", "Relay 3", "Relay 4" };
        for (String switchButton : relayUsage) {
            createPage.clickSectionSwitchButtonsByName("Relay Usage", switchButton, "");
        }

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.clickSectionSwitchButtonsByName("Optional Attributes", "Yes", "disableGroup");
        createPage.clickSectionSwitchButtonsByName("Optional Attributes", "Yes", "disableControl");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
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

        String[] addressUsage = { "Section", "Class", "Division" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }
        // MultiSelectCheckboxElement check = new MultiSelectCheckboxElement(driverExt, "addressUsage");

        createPage.getUtilityAddress().setInputValue(String.valueOf("254"));
        createPage.getSectionAddress().setInputValue(String.valueOf("256"));

        String[] classAddress = { "1", "2", "3", "15", "8", "9", "16" };
        for (String switchButton : classAddress) {
            createPage.clickSectionSwitchButtonsByName("Addressing", switchButton, "classAddress");
        }

        String[] divisionAddress = { "11", "2", "3", "15", "16", "9", "1" };
        for (String switchButton : divisionAddress) {
            createPage.clickSectionSwitchButtonsByName("Addressing", switchButton, "divisionAddress");
        }
        String[] relayUsage = { "Relay 2", "Relay 3", "Relay 4" };
        for (String switchButton : relayUsage) {
            createPage.clickSectionSwitchButtonsByName("Relay Usage", switchButton, "");
        }

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getSection("General");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_AddressUsageSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getSection("Address Usage");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_AddressUsageSectionLabelsCorrect() {
        String sectionName = "Address Usage";
        String expectedLabels = "Usage:";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String actualLabels = createPage.getSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_AddressingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getSection("Addressing");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        createPage.getType().selectItemByText("Versacom Group");

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }

        List<String> expectedLabels = new ArrayList<>(
                List.of("Utility Address:", "Section Address:", "Class Address:", "Division Address:", "Serial Address:"));
        List<String> actualLabels = createPage.getSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_RelayUsageSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getSection("Relay Usage");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_RelayUsageSectionLabelsCorrect() {
        String sectionName = "Relay Usage";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String expectedLabels = "Relay Usage:";
        String actualLabels = createPage.getSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_OptionalAttributesSectionTitleCorrect() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getSection("Optional Attributes");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void ldGrpCreateVersacom_OptionalAttributesSectionLabelsCorrect() {
        String sectionName = "Optional Attributes";
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
        List<String> actualLabels = createPage.getSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_UtilityAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Utility Address is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_UtilityAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("255");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_UtilityAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_UtilityAddressDefaultValueValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        assertThat(createPage.getUtilityAddress().getInputValue()).isEqualTo("1");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SerialAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }
        createPage.getSerialAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Serial Address is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SerialAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }
        createPage.getSerialAddress().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SerialAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }
        createPage.getSerialAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SectionAddressBlankValue() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.clickSectionSwitchButtonsByName("Address Usage", "Section", "");
        createPage.getSectionAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Section Address is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SectionAddressMaxRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.clickSectionSwitchButtonsByName("Address Usage", "Section", "");
        createPage.getSectionAddress().setInputValue("257");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_SectionAddressMinRangeValidation() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        createPage.clickSectionSwitchButtonsByName("Address Usage", "Section", "");
        createPage.getSectionAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_WhenUsageSerialThenAddressUsageSectionDivisionClassDisabled() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }

        MultiSelectCheckboxElement check = new MultiSelectCheckboxElement(driverExt, "addressUsage");
        List<String> expectedCheckboxDisabled =new ArrayList<>(List.of("true", "true", "true", "false"));
        List<String> actualCheckboxDisabled = check.isDisabled();
        assertThat(actualCheckboxDisabled).containsExactlyElementsOf(expectedCheckboxDisabled);
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreateVersacom_WhenUsageSectionDivisionClassSerialThenAddressingSectionClassDivisionDisabled() {
        createPage.getType().selectItemByText("Versacom Group");
        waitForLoadingSpinner();

        String[] addressUsage = { "Section", "Class", "Division", "Serial" };
        for (String switchButton : addressUsage) {
            createPage.clickSectionSwitchButtonsByName("Address Usage", switchButton, "");
        }

        List<String> expectedCheckboxDisabledStatus =new ArrayList<>(List.of("true", "true", "true", "false"));
        String[] sectionLabels = { "sectionAddress", "classAddress", "divisionAddress", "serialAddress"};
        
        String actualTextboxDisabled = "";
        List<String> actualCheckboxDisabledStatus= new ArrayList<>();
        for(String label: sectionLabels) {
            actualTextboxDisabled = createPage.getAddressingSectionSwitchButtonStatusByLabelName(label);
            actualCheckboxDisabledStatus.add(actualTextboxDisabled);
        }
        assertThat(actualCheckboxDisabledStatus).containsExactlyElementsOf(expectedCheckboxDisabledStatus);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
    }
}
