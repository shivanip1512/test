package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.elements.modals.TrendMarkerModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendEditPage;

public class TrendMarkerSetupTests extends SeleniumTestSetup {

    private TrendCreatePage trendCreatePage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        navigate(Urls.Tools.TREND_CREATE);
        trendCreatePage = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(trendCreatePage);    
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_Add_OpensCorrectModal() {
        final String EXP_MODAL_TITLE = "Add Marker";

        trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();

        String title = addMarkerModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_AddMarker_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();

        trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();

        List<String> labels = addMarkerModal.getFieldLabels();

        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Value:");
        softly.assertThat(labels.get(1)).contains("Label:");
        softly.assertThat(labels.get(2)).contains("Color:");
        softly.assertThat(labels.get(3)).contains("Axis:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_AddMarkerLabel_RequiredValidation() {
        final String EXPECTED_MSG = "Label is required.";

        trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();

        addMarkerModal.clickOk();

        String userMsg = addMarkerModal.getLabel().getValidationError();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_AddMarkerLabel_MaxLength40CharsSuccess() {
        final String EXPECTED_MAXLENGTH = "40";
        trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();

        String labelMaxlLength = addMarkerModal.getLabel().getMaxLength();

        assertThat(labelMaxlLength).isEqualTo(EXPECTED_MAXLENGTH);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_AddMarkerValue_InvalidCharValidation() {
        final String EXPECTED_MSG = "Not a valid value.";

        String value = "!@Test";

        trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();

        addMarkerModal.getValue().setInputValue(value);

        addMarkerModal.clickOk();

        String userMsg = addMarkerModal.getValue().getValidationError();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_EditMarker_OpensCorrectModal() {
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithMarker();
        JSONObject response = pair.getValue1();
        JSONObject jo = (JSONObject) response.getJSONArray("trendSeries").get(0);
        String label = jo.getString("label");
        final String EXP_MODAL_TITLE = "Edit " + label;

        Integer editTrendId = response.getInt("trendId");
        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);
        TrendEditPage trendEditPage = new TrendEditPage(this.driverExt, Urls.Tools.TREND_EDIT, editTrendId);

        trendEditPage.getTabElement().clickTabAndWait("Additional Options");
        TrendMarkerModal modal = trendEditPage.showAndWaitEditMarkerModal(EXP_MODAL_TITLE, 0);

        String title = modal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendMarkerSetup_EditMarkerField_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();

        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithMarker();
        JSONObject response = pair.getValue1();
        JSONObject jo = (JSONObject) response.getJSONArray("trendSeries").get(0);        
        Integer editTrendId = response.getInt("trendId");
        String label = jo.getString("label");

        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);
        TrendEditPage trendEditPage = new TrendEditPage(this.driverExt, Urls.Tools.TREND_EDIT, editTrendId);
        
        trendEditPage.getTabElement().clickTabAndWait("Additional Options");

        TrendMarkerModal modal = trendEditPage.showAndWaitEditMarkerModal("Edit " + label,  0);

        String color = modal.getColor().getSelectedColor();

        softly.assertThat(modal.getValue().getInputValue()).isEqualTo(Double.toString(jo.getDouble("multiplier")));
        softly.assertThat(modal.getLabel().getInputValue()).isEqualTo(label);
        softly.assertThat(modal.getAxis().getValueChecked()).isEqualTo(jo.getString("axis"));
        softly.assertThat(color).isEqualTo(jo.getString("color").toLowerCase());
        softly.assertAll();
    }

}
