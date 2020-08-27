package com.eaton.tests.tools.trends;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendPointBuilder;
import com.eaton.builders.tools.webtrends.TrendTypes;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendEditPage;

import io.restassured.response.ExtractableResponse;

@Test
public class TrendsPointSetupTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    TrendCreateBuilder.Builder trendCreateBuilder;
    private Integer trendId;
    String trendName;
    private TrendPointModal trendPointModal;
    private TrendEditPage trendEditPage;
    private TrendCreatePage trendCreatePage;
    private final int POINT_ID = 5157;
    private final String POINT_NAME = "BANK STATUS";
    private final String POINT_LABEL = "BANK STATUS LABEL";

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        // Create Trend with a Point
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(POINT_ID)
                        .withLabel(Optional.of(POINT_LABEL))
                        .withColor(Optional.of(TrendTypes.Color.BLUE))
                        .withStyle(Optional.of(TrendTypes.Style.LINE))
                        .withType(Optional.of(TrendTypes.Type.BASIC_TYPE))
                        .withAxis(Optional.of(TrendTypes.Axis.LEFT))
                        .withMultiplier(Optional.of(2.0))
                        .build() })
                .create();
        ExtractableResponse<?> response = pair.getValue1();
        trendId = response.path("trendId");
        trendName = response.path("name").toString();

        
        navigate(Urls.Tools.TREND_CREATE);
        trendCreatePage = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        refreshPage(trendCreatePage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_OpensCorrectModal() {
        String EXPECTED_MODAL_TITLE = "Add Point";
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        String actualModalTitle = trendPointModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_AllLabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> labels = trendPointModal.getFieldLabels();
        
        softly.assertThat(labels.size()).isEqualTo(9);
        softly.assertThat(labels.get(0)).isEqualTo("Point:");
        softly.assertThat(labels.get(1)).contains("Device:");
        softly.assertThat(labels.get(2)).contains("Label:");
        softly.assertThat(labels.get(3)).contains("Color:");
        softly.assertThat(labels.get(4)).contains("Style:");
        softly.assertThat(labels.get(5)).contains("Type:");
        softly.assertThat(labels.get(7)).contains("Axis:");
        softly.assertThat(labels.get(8)).contains("Multiplier:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_LabelRequiredValidation() {
        final String EXPECTED_MSG = "Label is required.";
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        // Set the empty value for the Label Input Field
        trendPointModal.getLabel().setInputValue("");
        trendPointModal.clickOkAndWait();

        String errorMsg = trendPointModal.getLabel().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_LabelMaxLengthCorrect() {
        final String ALLOWED_MAX_LENGTH = "40";
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        String maxLengthofLabel = trendPointModal.getLabel().getEditElement().getAttribute("maxlength");
        assertThat(maxLengthofLabel).isEqualTo(ALLOWED_MAX_LENGTH);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_PointRequiredValidation() {
        final String EXPECTED_MSG = "Point is required.";
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        trendPointModal.clickOkAndWait();
        String errorMsg = trendPointModal.getPoint().getValidationError("pointId");
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointMultiplier_InvalidValidation() {
        final String EXPECTED_MSG = "Not a valid value.";
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        // Put invalid chars in the Multiplier field and save
        trendPointModal.getMultiplier().setInputValue("abc@");
        trendPointModal.clickOkAndWait();
        String errorMsg = trendPointModal.getMultiplier().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointStyle_ContainsAllExpectedValues() {
        SoftAssertions softly = new SoftAssertions();
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> Styles = trendPointModal.getStyle().getOptionValues();
        softly.assertThat(Styles.size()).isEqualTo(3);
        softly.assertThat(Styles.get(0)).isEqualTo("Line");
        softly.assertThat(Styles.get(1)).isEqualTo("Step");
        softly.assertThat(Styles.get(2)).isEqualTo("Bar");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointType_ContainsAllExpectedValues() {
        SoftAssertions softly = new SoftAssertions();
        trendPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> Types = trendPointModal.getType().getOptionValues();
        softly.assertThat(Types.size()).isEqualTo(5);
        softly.assertThat(Types.get(0)).isEqualTo("Basic");
        softly.assertThat(Types.get(1)).isEqualTo("Usage");
        softly.assertThat(Types.get(2)).isEqualTo("Peak");
        softly.assertThat(Types.get(3)).isEqualTo("Yesterday");
        softly.assertThat(Types.get(4)).isEqualTo("Date");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_EditPoint_OpensCorrectModal() {
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        String actualModalTitle;
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
        trendPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE, 0);
        actualModalTitle = trendPointModal.getModalTitle();
        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendPointSetup_EditPoint_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
        trendPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE, 0);

        softly.assertThat(trendPointModal.getLabel().getInputValue()).isEqualTo(POINT_LABEL);
        softly.assertThat(trendPointModal.getAxis().getValueChecked()).isEqualTo("LEFT");
        softly.assertThat(trendPointModal.getMultiplier().getInputValue()).isEqualTo("2.0");
        softly.assertThat(trendPointModal.getStyle().getSelectedValue()).isEqualTo("Line");
        softly.assertThat(trendPointModal.getType().getSelectedValue()).isEqualTo("Basic");
        softly.assertThat(trendPointModal.getReadOnlyFieldValueByLabel("Device:")).isEqualTo("AT Cap Bank");
        softly.assertThat(trendPointModal.getPoint().getLinkValueDynamic()).isEqualTo(POINT_NAME);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_EditPoint_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
        trendPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE, 0);

        List<String> labels = trendPointModal.getFieldLabels();
        softly.assertThat(labels.size()).isEqualTo(9);
        softly.assertThat(labels.get(0)).isEqualTo("Point:");
        softly.assertThat(labels.get(1)).contains("Device:");
        softly.assertThat(labels.get(2)).contains("Label:");
        softly.assertThat(labels.get(3)).contains("Color:");
        softly.assertThat(labels.get(4)).contains("Style:");
        softly.assertThat(labels.get(5)).contains("Type:");
        softly.assertThat(labels.get(7)).contains("Axis:");
        softly.assertThat(labels.get(8)).contains("Multiplier:");
        softly.assertAll();
    }
}
