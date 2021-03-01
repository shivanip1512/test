package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupVersacomCreatePage;
import com.github.javafaker.Faker;

public class LoadGroupVersacomCreateTests extends SeleniumTestSetup {

    private LoadGroupVersacomCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupVersacomCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(createPage);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RequiredFieldsOnly_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Versacom " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");

        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AllFieldsyWithSerialAddress_Success() {   
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());        
        String name = "AT Versacom " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);      

        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");

        waitForLoadingSpinner();
        
        String commRoute = TestDbDataType.CommunicationRouteData.ACCU711.getId().toString();
        createPage.getCommunicationRoute().selectItemByValue(commRoute); 
        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);
        createPage.getName().setInputValue(name);      
        createPage.getSerialAddress().setInputValue(String.valueOf("40"));
        createPage.getRelayUsage().setTrueFalseByLabel("Relay 2", "RELAY_2", true);
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().selectValue("Yes");
        createPage.getDisableControl().selectValue("Yes");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AllFieldsWithoutSerialAddress_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Versacom " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");

        waitForLoadingSpinner();
        String commRoute = TestDbDataType.CommunicationRouteData.ACCU711.getId().toString();
        createPage.getCommunicationRoute().selectItemByValue(commRoute);

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Class", "CLASS", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Division", "DIVISION", true);
        
        createPage.getUtilityAddress().setInputValue(String.valueOf(faker.number().numberBetween(1, 254)));
        createPage.getSectionAddress().setInputValue(String.valueOf(faker.number().numberBetween(1, 255)));

        createPage.getClassAddress().setTrueFalseByLabel("1", "1", true);
        createPage.getDivisionAddress().setTrueFalseByLabel("11", "11", true);
        
        createPage.getRelayUsage().setTrueFalseByLabel("Relay 3", "RELAY_3", true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_GeneralSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_GeneralSection_LabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Address Usage");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSection_LabelsCorrect() {
        String sectionName = "Address Usage";
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        Section addressUsageSection = createPage.getPageSection(sectionName);
        
        assertThat(addressUsageSection).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressingSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Addressing");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressingSection_LabelsCorrect() {
        String sectionName = "Addressing";
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Class", "CLASS", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Division", "DIVISION", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);

        List<String> expectedLabels = new ArrayList<>(List.of("Utility Address:", "Section Address:", "Class Address:", "Division Address:", "Serial Address:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RelayUsageSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Relay Usage");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_RelayUsageSection_LabelsCorrect() {
        String sectionName = "Relay Usage";
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        String expectedLabels = "Relay Usage:";
        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
        assertThat(actualLabels).contains(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddress_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().clearInputValue();
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Utility Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddress_MaxRangeValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("255");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddress_MinRangeValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getUtilityAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getUtilityAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_UtilityAddress_DefaultValueCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        assertThat(createPage.getUtilityAddress().getInputValue()).isEqualTo("1");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddress_RequiredValidation() {
        setRefreshPage(true);
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);
        createPage.getSerialAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Serial Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddress_MaxRangeValidation() {
        setRefreshPage(true);
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);
        createPage.getSerialAddress().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SerialAddress_MinRangeValidation() {
        setRefreshPage(true);
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);
        createPage.getSerialAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Must be between 1 and 99,999.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddress_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getSectionAddress().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Section Address is required.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddress_MaxRangeValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getSectionAddress().setInputValue("257");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_SectionAddress_MinRangeValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getSectionAddress().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getSectionAddress().getValidationError()).isEqualTo("Must be between 0 and 256.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSerial_SectionClassDivisionDisabled() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);
              
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Section")).isTrue();
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Class")).isTrue();
        softly.assertThat(createPage.getAddressUsage().isValueDisabled("Division")).isTrue();        
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateVersacom_AddressUsageSectionDivisionClassSerialSelected_AddressingSectionClassDivisionDisabled() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        createPage.getType().selectItemByValue("LM_GROUP_VERSACOM");
        waitForLoadingSpinner();

        createPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Class", "CLASS", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Division", "DIVISION", true);
        createPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);

        softly.assertThat(createPage.getClassAddress().allValuesDisabled()).isTrue();
        softly.assertThat(createPage.getSectionAddress().isDisabled()).isFalse();
        softly.assertThat(createPage.getDivisionAddress().allValuesDisabled()).isTrue();
        softly.assertAll();
    }
}
