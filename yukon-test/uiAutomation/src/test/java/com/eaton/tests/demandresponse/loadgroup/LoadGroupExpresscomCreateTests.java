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
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupExpresscomCreatePage;

public class LoadGroupExpresscomCreateTests extends SeleniumTestSetup {

    private LoadGroupExpresscomCreatePage createPage;
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
        createPage = new LoadGroupExpresscomCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_AllFieldsSuccessfullyWithSerialAddress() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Expresscom " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Expresscom Group");

        waitForLoadingSpinner();
        createPage.getCommunicationRoute().selectItemByText("a_REPEATER-801");

        createPage.getGeographicalAddress().setTrueFalseByName("Serial", true);

        createPage.getSerial().setInputValue(String.valueOf(randomNum.nextInt(999999999)));

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        confirmModal.clickCancelAndWait();

        createPage.getLoads().setTrueFalseByName("Load 8", true);
        createPage.getProgram().setInputValue(String.valueOf(randomNum.nextInt(254)));
        createPage.getSplinter().setInputValue(String.valueOf(randomNum.nextInt(254)));

        createPage.getControlPriority().selectItemByText("Medium");
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(false);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_AllFieldsSuccessfullyWithoutSerialAddress() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Expresscom " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Expresscom Group");

        waitForLoadingSpinner();
        createPage.getCommunicationRoute().selectItemByText("a_RTC");

        createPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
        createPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
        createPage.getGeographicalAddress().setTrueFalseByName("Feeder", true);
        createPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
        createPage.getGeographicalAddress().setTrueFalseByName("User", true);

        createPage.getSpid().setInputValue(String.valueOf(randomNum.nextInt(65534)));
        createPage.getGeo().setInputValue(String.valueOf(randomNum.nextInt(65534)));
        createPage.getSubstation().setInputValue(String.valueOf(randomNum.nextInt(65534)));
        createPage.getFeeder().setTrueFalseByName("10", true);
        createPage.getZip().setInputValue(String.valueOf(randomNum.nextInt(65534)));
        createPage.getUser().setInputValue(String.valueOf(randomNum.nextInt(65534)));

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        confirmModal.clickCancelAndWait();

        createPage.getLoads().setTrueFalseByName("Load 8", true);
        createPage.getProgram().setInputValue(String.valueOf(randomNum.nextInt(254)));
        createPage.getSplinter().setInputValue(String.valueOf(randomNum.nextInt(254)));

        createPage.getControlPriority().selectItemByText("Medium");
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(true);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeographicalAddressSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Geographical Address");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeographicalAddressSectionLabelsCorrect() {
        String sectionName = "Geographical Address";
        String expectedLabels = "Address Usage:";
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeographicalAddressingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        Section geographicalAddressingSection = createPage.getPageSection("Geographical Addressing");

        assertThat(geographicalAddressingSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeographicalAddressingSectionLabelsCorrect() {
        String sectionName = "Geographical Addressing";
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
        createPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
        createPage.getGeographicalAddress().setTrueFalseByName("Feeder", true);
        createPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
        createPage.getGeographicalAddress().setTrueFalseByName("User", true);
        createPage.getGeographicalAddress().setTrueFalseByName("Serial", true);

        List<String> expectedLabels = new ArrayList<>(
                List.of("SPID:", "GEO:", "Substation:", "Feeder:", "ZIP:", "User:", "Serial:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Load Address");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressSectionLabelsCorrect() {
        String sectionName = "Load Address";
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        String expectedLabels = "Usage:";
        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Load Addressing");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressingSectionLabelsCorrect() {
        String sectionName = "Load Addressing";
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        confirmModal.clickCancelAndWait();

        List<String> expectedLabels = new ArrayList<>(
                List.of("Send Loads in Control Message:", "Loads:", "Program:", "Splinter:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_OptionalAttributesSectionTitleCorrect() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Optional Attributes");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_OptionalAttributesSectionLabelsCorrect() {
        String sectionName = "Optional Attributes";
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(
                List.of("Control Priority:", "kW Capacity:", "Disable Group:", "Disable Control:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SpidAddressBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getSpid().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSpid().getValidationError()).isEqualTo("SPID is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SpidMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getSpid().setInputValue("65535");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSpid().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SpidMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getSpid().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSpid().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SpidDefaultValueValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        assertThat(createPage.getSpid().getInputValue()).isEqualTo("1");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeoBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getGeo().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getGeo().getValidationError()).isEqualTo("Geo is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeoMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getGeo().setInputValue("65535");
        createPage.getSaveBtn().click();

        assertThat(createPage.getGeo().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_GeoMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getGeo().setInputValue("0");
        createPage.getSaveBtn().click();

        assertThat(createPage.getGeo().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SubstationBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSubstation().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSubstation().getValidationError()).isEqualTo("Substation is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SubstationMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSubstation().setInputValue("65535");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSubstation().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SubstationMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSubstation().setInputValue("0");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSubstation().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_FeederField16Switches() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Feeder", true);
        assertThat(createPage.getFeeder().getSwitchCount()).isEqualTo(16);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadsField8Switches() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        assertThat(createPage.getLoads().getSwitchCount()).isEqualTo(8);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ZipBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getZip().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getZip().getValidationError()).isEqualTo("Zip is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ZipMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getZip().setInputValue("16777219");
        createPage.getSaveBtn().click();

        assertThat(createPage.getZip().getValidationError()).isEqualTo("Must be between 1 and 16,777,214.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ZipMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getZip().setInputValue("0");
        createPage.getSaveBtn().click();

        assertThat(createPage.getZip().getValidationError()).isEqualTo("Must be between 1 and 16,777,214.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_UserBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("User", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getUser().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUser().getValidationError()).isEqualTo("User is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_UserMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("User", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getUser().setInputValue("65535");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUser().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_UserMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("User", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getUser().setInputValue("0");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUser().getValidationError()).isEqualTo("Must be between 1 and 65,534.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SerialBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Serial", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSerial().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerial().getValidationError()).isEqualTo("Serial Number is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SerialMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Serial", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSerial().setInputValue("1000000000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerial().getValidationError()).isEqualTo("Must be between 0 and 999,999,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SerialMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getGeographicalAddress().setTrueFalseByName("Serial", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getSerial().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerial().getValidationError()).isEqualTo("Must be between 0 and 999,999,999.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressingUsageLoadValidateErrorAtTop() {
        final String EXPECTED_MSG = "At least 1 load group must be selected when LOAD usage is checked.";
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Create Load Group", Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ValidateSendLoadsInControlMessageValueForLoadAddressUsageLoadSelected() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("Yes");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ValidateSendLoadsInControlMessageValueForLoadAddressUsageLoadNotSelected() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Load", false);
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("No");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressUsageLoadProgramSelectedClickedYesInModal() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("Yes");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressUsageLoadProgramSelectedClickedNoInModal() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickOkAndWait();
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("No");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressUsageLoadProgramSplinterSelectedClickedYesInModal() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);

        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        confirmModal.clickCancelAndWait();
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("Yes");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_LoadAddressUsageLoadProgramSplinterSelectedClickedNoInModal() {
        createPage.getType().selectItemByText("Expresscom Group");

        createPage.getLoadAddress().setTrueFalseByName("Load", true);
        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt,
                Optional.of("Load level group addressing vs. load number addressing"), Optional.of("addressing-popup"));
        confirmModal.clickCancelAndWait();
        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        confirmModal.clickOkAndWait();
        assertThat(createPage.getSendLoadsInControlMessageText()).isEqualTo("No");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ProgramBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        createPage.getProgram().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getProgram().getValidationError()).isEqualTo("Program is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ProgramMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        createPage.getProgram().setInputValue("300");
        createPage.getSaveBtn().click();

        assertThat(createPage.getProgram().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ProgramMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Program", true);
        createPage.getProgram().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getProgram().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SplinterBlankValue() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        createPage.getSplinter().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSplinter().getValidationError()).isEqualTo("Splinter is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SplinterMaxRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        createPage.getSplinter().setInputValue("16777219");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSplinter().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_SplinterMinRangeValidation() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();

        createPage.getLoadAddress().setTrueFalseByName("Splinter", true);
        createPage.getSplinter().setInputValue("0");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSplinter().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateExpresscom_ControlPriorityDropDownContainsAllExpectedValues() {
        createPage.getType().selectItemByText("Expresscom Group");
        waitForLoadingSpinner();
        createPage.getControlPriority().selectItemByText("Medium");
        List<String> expectedDropDownValues = new ArrayList<>(List.of("Default", "Medium", "High", "Highest"));
        List<String> actualDropDownValues = createPage.getControlPriority().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }
}
