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

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupRippleCreatePage(driverExt);
    }
}
