package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendMarkerBuilder;
import com.eaton.builders.tools.webtrends.TrendTypes;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icon;
import com.eaton.elements.modals.TrendAddMarkerModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendEditPage;
import io.restassured.response.ExtractableResponse;

public class TrendMarkerSetupTests extends SeleniumTestSetup{

	private TrendCreatePage trendCreatePage;
	private TrendEditPage trendEditPage;
    private DriverExtensions driverExt;
    private int trendId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withLabel(Optional.empty())
                        .withColor(Optional.of(TrendTypes.Color.BLUE))
                        .withMultiplier(Optional.empty())
                        .build() })
                .create();
        
        ExtractableResponse<?> response = pair.getValue1();
        trendId = response.path("trendId");
        
        navigate(Urls.Tools.TREND_CREATE);
        trendCreatePage = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
        trendEditPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
    }

    @AfterMethod
    public void afterMethod() {
        refreshPage(trendCreatePage);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_Add_OpensCorrectModal() {
    	final String EXP_MODAL_TITLE = "Add Marker";
    			
    	trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();
   
    	String title = addMarkerModal.getModalTitle();
    	
        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabels_Correct() {
    	SoftAssertions softly = new SoftAssertions();
    	
    	trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();
    	
    	List<String> labels = addMarkerModal.getFieldLabels();
    	
    	softly.assertThat(labels.size()).isEqualTo(4);
    	softly.assertThat(labels.get(0)).isEqualTo("Value:");
        softly.assertThat(labels.get(1)).contains("Label:");
        softly.assertThat(labels.get(2)).contains("Color:");
        softly.assertThat(labels.get(3)).contains("Axis:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabel_RequiredValidation() {
    	final String EXPECTED_MSG = "Label is required.";
    	
    	trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();
    	
    	addMarkerModal.clickOkAndWait();
    	
    	String userMsg = addMarkerModal.getLabel().getValidationError();
    	
    	assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabel_MaxLength40CharsSuccess() {
    	final String EXPECTED_MAXLENGTH = "40";
    	trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();
    	
    	String labelMaxlLength = addMarkerModal.getLabelMaxLength();
    	
    	assertThat(labelMaxlLength).isEqualTo(EXPECTED_MAXLENGTH);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerValue_InvalidCharValidation() {
    	final String EXPECTED_MSG = "Not a valid value.";
    	
    	String value = "!@Test";
    	
    	trendCreatePage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = trendCreatePage.showAndWaitAddMarkerModal();
    	
    	addMarkerModal.getValue().setInputValue(value);
    	
    	addMarkerModal.clickOkAndWait();
    	
    	String userMsg = addMarkerModal.getValue().getValidationError();
    	
    	assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_EditMarker_OpensCorrectModal() {
    	Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withAxis(Optional.empty())
                        .build() })
                .create();
        
        ExtractableResponse<?> response = pair.getValue1();
    	
        JSONObject request = pair.getValue0();
        JSONObject jo =  (JSONObject) request.getJSONArray("trendSeries").get(0);
    	
        String label = jo.getString("label");
        
    	final String EXP_MODAL_TITLE = "Edit " + label;
    	
    	trendId = response.path("trendId");
        
    	navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
   
    	trendEditPage.getTabElement().clickTabAndWait("Additional Options");
    	
    	WebTableRow row = trendCreatePage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);
        
        TrendAddMarkerModal modal = new TrendAddMarkerModal(this.driverExt, Optional.of(EXP_MODAL_TITLE), Optional.empty());
        
        String title = modal.getModalTitle();
    	
        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
 
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_EditMarkerFieldValues_Correct() {
    	SoftAssertions softly = new SoftAssertions();
    	
    	String color = null;
    	
    	Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendMarkerBuilder.Builder()
                        .withLabel(Optional.empty())
                        .withColor(Optional.of(TrendTypes.Color.BLUE))
                        .withMultiplier(Optional.empty())
                        .withAxis(Optional.empty())
                        .build() })
                .create();
        
        ExtractableResponse<?> response = pair.getValue1();
        
        JSONObject request = pair.getValue0();
        JSONObject jo =  (JSONObject) request.getJSONArray("trendSeries").get(0);
        
        trendId = response.path("trendId");
        
    	navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
   
    	trendEditPage.getTabElement().clickTabAndWait("Additional Options");

    	WebTableRow row = trendEditPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);
        
        TrendAddMarkerModal modal = new TrendAddMarkerModal(this.driverExt, Optional.of("Edit " + jo.getString("label")), Optional.empty());
        
        if ((modal.getColor()).equalsIgnoreCase("background-color: rgb(0, 136, 242);")) color = "BLUE";
    	
        softly.assertThat(modal.getValue().getInputValue()).isEqualTo(Double.toString(jo.getDouble("multiplier")));
        softly.assertThat(modal.getLabel().getInputValue()).isEqualTo(jo.getString("label"));
        softly.assertThat(modal.getAxis().getValueChecked()).isEqualTo(jo.getString("axis"));
        softly.assertThat(color).isEqualTo(jo.getString("color"));
        softly.assertAll();
    }
    
}
