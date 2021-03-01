package com.eaton.tests.tools.trends;

import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.eaton.builders.tools.trends.TrendCreateBuilder;
import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;

@Test
public class TrendsPointSetupTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    TrendCreateBuilder.Builder trendCreateBuilder;
    private Integer trendId;
    String trendName;
    private TrendPointModal trendPointModal;
    private TrendEditPage trendEditPage;
    private String POINT_NAME;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        // Create Trend with a Point
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.empty());

        response = pair.getValue1();
        trendId = response.getInt("trendId");
        trendName = response.getString("name");
        POINT_NAME = "Analog Point for Create Trend";

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(trendEditPage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendPointSetup_AddPoint_OpensCorrectModal() {
        String EXPECTED_MODAL_TITLE = "Add Point";
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        String actualModalTitle = trendPointModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS})
    public void trendPointSetup_FieldLabels_Correct() {
        SoftAssertions softly = new SoftAssertions();

        trendPointModal = trendEditPage.showAndWaitAddPointModal();
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

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Label_RequiredValidation() {
        final String EXPECTED_MSG = "Label is required.";
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        trendPointModal.getLabel().clearInputValue();
        trendPointModal.clickOk();

        String errorMsg = trendPointModal.getLabel().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Label_MaxLength40Chars() {
        final String ALLOWED_MAX_LENGTH = "40";
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        String maxLengthofLabel = trendPointModal.getLabel().getEditElement().getAttribute("maxlength");
        
        assertThat(maxLengthofLabel).isEqualTo(ALLOWED_MAX_LENGTH);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Point_RequiredValidation() {
        final String EXPECTED_MSG = "Point is required.";
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        trendPointModal.clickOk();
        String errorMsg = trendPointModal.getPoint().getValidationError("pointId");
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Multiplier_InvalidValidation() {
        final String EXPECTED_MSG = "Not a valid value.";
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        // Put invalid chars in the Multiplier field and save
        trendPointModal.getMultiplier().setInputValue("abc@");
        trendPointModal.clickOk();
        
        String errorMsg = trendPointModal.getMultiplier().getValidationError();

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Style_ExpectedValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        trendPointModal = trendEditPage.showAndWaitAddPointModal();

        List<String> Styles = trendPointModal.getStyle().getOptionValues();

        softly.assertThat(Styles.size()).isEqualTo(3);
        softly.assertThat(Styles.get(0)).isEqualTo("Line");
        softly.assertThat(Styles.get(1)).isEqualTo("Step");
        softly.assertThat(Styles.get(2)).isEqualTo("Bar");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_Type_ExpectedValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        trendPointModal = trendEditPage.showAndWaitAddPointModal();
        List<String> Types = trendPointModal.getType().getOptionValues();

        softly.assertThat(Types.size()).isEqualTo(5);
        softly.assertThat(Types.get(0)).isEqualTo("Basic");
        softly.assertThat(Types.get(1)).isEqualTo("Usage");
        softly.assertThat(Types.get(2)).isEqualTo("Peak");
        softly.assertThat(Types.get(3)).isEqualTo("Yesterday");
        softly.assertThat(Types.get(4)).isEqualTo("Date");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendPointSetup_EditPoint_OpensCorrectModal() {
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;

        trendPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE, 0);

        String actualModalTitle = trendPointModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(EXPECTED_MODAL_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TRENDS })
    public void trendPointSetup_EditPoint_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        final String EXPECTED_MODAL_TITLE = "Edit " + POINT_NAME;
        
        JSONArray trendSeries = response.getJSONArray("trendSeries");
        JSONObject point = (JSONObject) trendSeries.get(0);

        trendPointModal = trendEditPage.showAndWaitEditPointModal(EXPECTED_MODAL_TITLE, 0);
        Double multiplier = point.getDouble("multiplier");

        softly.assertThat(trendPointModal.getLabel().getInputValue()).isEqualTo(point.getString("label"));
        softly.assertThat(trendPointModal.getAxis().getValueChecked()).isEqualTo(point.getString("axis"));
        softly.assertThat(trendPointModal.getMultiplier().getInputValue()).isEqualTo(multiplier.toString());
        softly.assertThat(trendPointModal.getStyle().getOptionValue()).isEqualTo(point.getString("style"));
        softly.assertThat(trendPointModal.getType().getOptionValue()).isEqualTo(point.getString("type"));
        softly.assertThat(trendPointModal.getReadOnlyFieldValueByLabel("Device:")).isEqualTo("RTU for Trends");
        softly.assertThat(trendPointModal.getPoint().getLinkValue()).isEqualTo(POINT_NAME);
        softly.assertAll();
    }
}
