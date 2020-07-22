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
import com.eaton.pages.demandresponse.LoadGroupRippleCreatePage;

public class LoadGroupRippleCreateTests extends SeleniumTestSetup {

    private LoadGroupRippleCreatePage createPage;
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
        createPage = new LoadGroupRippleCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_RequiredFieldsOnlySuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Ripple " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Ripple Group");

        waitForLoadingSpinner();
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
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
        createPage.getCommunicationRoute().selectItemByText("a_CCU-711");

        createPage.getAddressUsage().setTrueFalseByValue("Section", true);
        createPage.getAddressUsage().setTrueFalseByValue("Class", true);
        createPage.getAddressUsage().setTrueFalseByValue("Division", true);
        
        createPage.getUtilityAddress().setInputValue(String.valueOf(randomNum.nextInt(254)));
        createPage.getSectionAddress().setInputValue(String.valueOf(randomNum.nextInt(255)));

        createPage.getClassAddress().setTrueFalseByValue("1", true);
        createPage.getDivisionAddress().setTrueFalseByValue("11", true);
        
        createPage.getRelayUsage().setTrueFalseByValue("Relay_3", true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_AddressUsageSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("Address Usage");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        createPage.getType().selectItemByText("Ripple Group");

        createPage.getAddressUsage().setTrueFalseByValue("Section", true);
        createPage.getAddressUsage().setTrueFalseByValue("Class", true);
        createPage.getAddressUsage().setTrueFalseByValue("Division", true);
        createPage.getAddressUsage().setTrueFalseByValue("Serial", true);

        List<String> expectedLabels = new ArrayList<>(
                List.of("Utility Address:", "Section Address:", "Class Address:", "Division Address:", "Serial Address:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_OptionalAttributesSectionTitleCorrect() {
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        Section generalSection = createPage.getPageSection("Optional Attributes");
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateRipple_OptionalAttributesSectionLabelsCorrect() {
        String sectionName = "Optional Attributes";
        createPage.getType().selectItemByText("Ripple Group");
        waitForLoadingSpinner();

        List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupRippleCreatePage(driverExt);
    }
}
