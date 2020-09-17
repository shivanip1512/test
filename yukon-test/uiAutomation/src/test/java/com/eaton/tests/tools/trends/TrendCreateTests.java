package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.elements.Section;
import com.eaton.elements.modals.TrendMarkerModal;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendsListPage;
import com.github.javafaker.Faker;

public class TrendCreateTests extends SeleniumTestSetup {

    private Faker faker;

    private TrendCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();

        navigate(Urls.Tools.TREND_CREATE);
        createPage = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Trend";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendCreate_AllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Trend " + timeStamp;

        String point = "Analog Point for Create Trend";
        String label = "AT Marker";

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);

        // Adding Point Setup
        createPage.getTabElement().clickTabAndWait("Setup");
        TrendPointModal addPointModal = createPage.showAndWaitAddPointModal();
        SelectPointModal selectPointModal = addPointModal.showAndWaitSelectPointModal();
        selectPointModal.selectPoint(point, Optional.of("5231"));
        selectPointModal.clickOkAndWaitForModalToClose();
        addPointModal.clickOkAndWaitForModalToClose();

        // Adding Marker Setup
        createPage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
        addMarkerModal.getLabel().setInputValue(label);
        addMarkerModal.clickOkAndWaitForModalToClose();

        createPage.getSave().click();

        waitForPageToLoad(name, Optional.of(3));

        TrendsListPage listPage = new TrendsListPage(driverExt);

        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendCreate_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Trend " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getSave().click();

        TrendsListPage listPage = new TrendsListPage(driverExt);

        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Tab_TitlesCorrect() {
        SoftAssertions softly = new SoftAssertions();

        List<String> titles = createPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Setup");
        softly.assertThat(titles.get(1)).isEqualTo("Additional Options");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_GeneralSection_TitleCorrect() {
        String tab = "Setup";

        createPage.getTabElement().clickTabAndWait(tab);

        Section generalSection = createPage.getGeneralSection();

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_GeneralSection_LabelsCorrect() {
        String tab = "Setup";

        createPage.getTabElement().clickTabAndWait(tab);

        List<String> expectedLabels = new ArrayList<>(List.of("Name:"));
        List<String> actualLabels = createPage.getGeneralSection().getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_PointSetupSection_TitleCorrect() {
        String tab = "Setup";

        createPage.getTabElement().clickTabAndWait(tab);

        Section generalSection = createPage.getPointSetupSection();

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_PointSection_TableHeadersCorrect() {
        String tab = "Setup";

        createPage.getTabElement().clickTabAndWait(tab);

        List<String> expectedLabels = new ArrayList<>(List.of("Device", "Point Name", "Label", "Color", "Axis", "Type", "Multiplier", "Style", ""));
        List<String> actualLabels = createPage.getPointSetupTable().getListTableHeaders();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_MarkerSetupSection_TitleCorrect() {
        String tab = "Additional Options";

        createPage.getTabElement().clickTabAndWait(tab);

        Section generalSection = createPage.getMarkerSetupSection();

        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_MarkerSetupTableHeaders_Correct() {
        String tab = "Additional Option";

        createPage.getTabElement().clickTabAndWait(tab);

        List<String> expectedLabels = new ArrayList<>(List.of("Label", "Color", "Axis", "Value", ""));

        List<String> actualLabels = createPage.getMarkerSetupTable().getListTableHeaders();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Name_RequiredValidation() {
        final String EXPECTED_MSG = "Name is required.";

        createPage.getSave().click();

        String errorMsg = createPage.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Name_AlreadyExistsValidation() {
        String trendName;

        final String EXPECTED_MSG = "Name already exists";

        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();

        JSONObject response = pair.getValue1();

        trendName = response.getString("name");

        navigate(Urls.Tools.TREND_CREATE);

        createPage.getName().setInputValue(trendName);
        createPage.getSave().click();

        String errorMsg = createPage.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Cancel_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Trend";

        createPage.getCancel().click();

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).contains(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Name_InvalidCharsValidation() {
        String name = "AT Trends " + "/ \\ , ' \" |";

        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        createPage.getName().setInputValue(name);
        createPage.getSave().click();

        String errorMsg = createPage.getName().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_Name_MaxLength40Chars() {
        String name = "AT Trend" + faker.number().digits(32);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getName().setInputValue(name);
        createPage.getSave().click();

        TrendsListPage listPage = new TrendsListPage(driverExt);

        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}