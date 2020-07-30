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

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupRippleCreatePage;

public class LoadGroupRippleCreateTests extends SeleniumTestSetup {

    private LoadGroupRippleCreatePage createPage;
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
        createPage = new LoadGroupRippleCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_AllFieldsSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Ripple " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Ripple Group");

        waitForLoadingSpinner();
        createPage.getCommunicationRoute().selectItemByText("a_CCU-721");

        createPage.getShedTime().selectItemByText("15 minutes");
        createPage.getGroup().selectItemByText("2.01");
        createPage.getAreaCode().selectItemByText("Minnkota");

        createPage.getControlSwitchElement().setTrueFalseByBitNo(1, true);
        createPage.getControlSwitchElement().setTrueFalseByBitNo(33, true);

        createPage.getRestoreSwitchElement().setTrueFalseByBitNo(10, true);
        createPage.getRestoreSwitchElement().setTrueFalseByBitNo(34, true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_AddressingSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Addressing");

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(
                List.of("Shed Time:", "Group:", "Area Code:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_OptionalAttributesSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Optional Attributes");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_OptionalAttributesSectionLabelsCorrect() {
        String sectionName = "Optional Attributes";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_CommunicationRouteDropDownContainsAllExpectedValues() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("a_CCU-710A", "a_CCU-711", "a_CCU-721", "a_LCU-EASTRIVER", "a_PAGING TAP TERMINAL", "a_REPEATER-800",
                        "a_REPEATER-801", "a_REPEATER-900", "a_REPEATER-902", "a_REPEATER-921", "a_RTC", "a_RTU-LMI",
                        "a_SNPP-TERMINAL", "a_TCU-5000", "a_TCU-5500", "a_WCTP-TERMINAL",
                        "a_XML"));
        List<String> actualDropDownValues = createPage.getCommunicationRoute().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_ShedTimeDropDownContainsAllExpectedValues() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("Continuous Latch", "7 minutes 30 seconds", "15 minutes", "30 minutes", "1 hour"));
        List<String> actualDropDownValues = createPage.getShedTime().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_GroupDropDownContainsAllExpectedValues() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("TST", "1.00", "1.01", "1.02", "2.00", "2.01", "2.02", "2.03", "2.04", "3.00",
                        "3.01", "3.06", "3.07", "3.09", "3.01_3.09", "4.00", "4.01", "4.02", "6.00", "6.01", "6.06"));
        List<String> actualDropDownValues = createPage.getGroup().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_AreaCodeTimeDropDownContainsAllExpectedValues() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("Universal", "Minnkota", "Beltrami", "Cass County", "Cavalier Rural", "Clearwater-Polk",
                        "NoDak Rural", "North Star", "PKM Electric", "Red Lake", "Red River Valley", "Roseau Electric",
                        "Sheyenne Valley", "Wild Rice", "NMPA"));
        List<String> actualDropDownValues = createPage.getAreaCode().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Double Orders");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersSectionLabelsCorrect() {
        String sectionName = "Double Orders";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("Control:", "Restore:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersControlField34Switches() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        assertThat(createPage.getControlSwitchElement().getSwitchCount()).isEqualTo(34);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersRestoreField34Switches() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        assertThat(createPage.getControlSwitchElement().getSwitchCount()).isEqualTo(34);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersControlField1stRowLabels() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(
                List.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        List<String> actualLabels = createPage.getControlLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateRipple_DoubleOrdersRestoreField1stRowLabels() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(
                List.of("9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"));
        List<String> actualLabels = createPage.getRestoreLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupRippleCreatePage(driverExt);
    }
}
