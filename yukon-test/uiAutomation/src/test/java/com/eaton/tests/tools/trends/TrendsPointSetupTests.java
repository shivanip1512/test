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
import com.eaton.elements.modals.TrendAddPointModal;
import com.eaton.elements.modals.TrendEditPointModal;
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
    private TrendAddPointModal trendAddPointModal;
    private TrendEditPointModal trendEditPointModal;
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
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        // trendAddPointModal =new TrendAddPointModal(driverExt,Optional.empty(), Optional.of("js-add-point-dialog"));
        String actualModalTitle = trendAddPointModal.getModalTitle();
        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_AllLabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> labels = trendAddPointModal.getFieldLabels();
        
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
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        // Set the empty value for the Label Input Field
        trendAddPointModal.getLabel().setInputValue("");
        trendAddPointModal.clickOkAndWait();

        String errorMsg = trendAddPointModal.getLabel().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_LabelMaxLengthCorrect() {
        final String ALLOWED_MAX_LENGTH = "40";
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        String maxLengthofLabel = trendAddPointModal.getLabel().getEditElement().getAttribute("maxlength");
        assertThat(maxLengthofLabel).isEqualTo(ALLOWED_MAX_LENGTH);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPoint_PointRequiredValidation() {
        final String EXPECTED_MSG = "Point is required.";
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        trendAddPointModal.clickOkAndWait();
        String errorMsg = trendAddPointModal.getPoint().getValidationError("pointId");
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointMultiplier_InvalidValidation() {
        final String EXPECTED_MSG = "Not a valid value.";
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        // Put invalid chars in the Multiplier field and save
        trendAddPointModal.getMultiplier().setInputValue("abc@");
        trendAddPointModal.clickOkAndWait();
        String errorMsg = trendAddPointModal.getMultiplier().getValidationError();
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointStyle_ContainsAllExpectedValues() {
        SoftAssertions softly = new SoftAssertions();
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> Styles = trendAddPointModal.getStyle().getOptionValues();
        softly.assertThat(Styles.size()).isEqualTo(3);
        softly.assertThat(Styles.get(0)).isEqualTo("Line");
        softly.assertThat(Styles.get(1)).isEqualTo("Step");
        softly.assertThat(Styles.get(2)).isEqualTo("Bar");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_AddPointType_ContainsAllExpectedValues() {
        SoftAssertions softly = new SoftAssertions();
        trendAddPointModal = trendCreatePage.showAndWaitAddPointModal();
        List<String> Types = trendAddPointModal.getType().getOptionValues();
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
        trendEditPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE);
        actualModalTitle = trendEditPointModal.getModalTitle();
        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendPointSetup_EditPoint_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
        trendEditPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE);

        softly.assertThat(trendEditPointModal.getLabel().getInputValue()).isEqualTo(POINT_LABEL);
        softly.assertThat(trendEditPointModal.getAxis().getValueChecked()).isEqualTo("LEFT");
        softly.assertThat(trendEditPointModal.getMultiplier().getInputValue()).isEqualTo("2.0");
        softly.assertThat(trendEditPointModal.getStyle().getSelectedValue()).isEqualTo("Line");
        softly.assertThat(trendEditPointModal.getType().getSelectedValue()).isEqualTo("Basic");
        softly.assertThat(trendEditPointModal.getReadOnlyFieldValueByLabel("Device:")).isEqualTo("AT Cap Bank");
        softly.assertThat(trendEditPointModal.getPoint().getLinkValueDynamic()).isEqualTo(POINT_NAME);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendPointSetup_EditPoint_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
        trendEditPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE);

        List<String> labels = trendEditPointModal.getFieldLabels();
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
