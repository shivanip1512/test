package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icon;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.TrendMarkerModal;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;
import io.restassured.response.ExtractableResponse;

public class TrendEditTests extends SeleniumTestSetup {

    private TrendEditPage editPage;
    private DriverExtensions driverExt;
    private int trendId;
    private String trendName;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        Pair<JSONObject, ExtractableResponse<?>> pair = TrendCreateService.buildAndCreateTrendAllFields();
        
        ExtractableResponse<?> response = pair.getValue1();
        
        trendId = response.path("trendId");
        trendName = response.path("name").toString();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
    }
    
    @AfterMethod
    public void afterMethod() {
        refreshPage(editPage);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void editTrend_PageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Trend: " + trendName;
        String actualPageTitle;

        actualPageTitle = editPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void editTrend_AllFields_Success() {
        String editTrendName = "EditTrendTest " + timeStamp;
        
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendAllFields();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer editTrendId = response.path("trendId");

        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);
        
        editPage.getName().setInputValue(editTrendName);
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(editTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_Name_RequiredValidation() {
        editPage.getName().setInputValue("");
        editPage.getSave().click();
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_Name_AlreadyExistsValidation() {
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getName().setInputValue(trendName);
        editPage.getSave().click();
        
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name already exists");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_RemovePoint_Success() {        
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty());
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.REMOVE);
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_RemoveMarker_Success() {
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendWithMarker();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getAdditionalOptionsTab().click();
        
        WebTableRow row = editPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.REMOVE);
        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_AddPoint_Success() {
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        TrendPointModal modal = editPage.showAndWaitAddPointModal();
        SelectPointModal pointModal = modal.showAndWaitSelectPointModal();
        pointModal.selectPoint("Analog Point for Create Trend", Optional.of("5231"));
        pointModal.clickOkAndWait();
        modal.clickOkAndWait();
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_AddMarker_Success() {
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getAdditionalOptionsTab().click();
        
        TrendMarkerModal modal = editPage.showAndWaitAddMarkerModal();
        modal.getLabel().setInputValue("Test label");
        modal.clickOkAndWait();

        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_EditPoint_Success() {
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty());
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);

        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);

        TrendPointModal modal = new TrendPointModal(this.driverExt, Optional.of("Edit Month History"), Optional.empty());
        modal.getLabel().setInputValue("Edit Point Label");
        modal.clickOkAndWait();

        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_EditMarker_Success() {
        String markerLabel = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        Pair<JSONObject, ExtractableResponse<?>> responses = TrendCreateService.buildAndCreateTrendWithMarker();
        
        ExtractableResponse<?> response = responses.getValue1();
        Integer newTrendId = response.path("trendId");
        String newTrendName = response.path("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);

        editPage.getAdditionalOptionsTab().click();
        TrendMarkerModal markerModal = editPage.showAndWaitEditMarkerModal("Edit " + markerLabel, 0);        

        markerModal.getLabel().setInputValue("Edit Marker Label");
        markerModal.clickOkAndWait();

        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void editTrend_Cancel_NavigatesToCorrectUrl() {
        String expectedURL = getBaseUrl() + Urls.Tools.TRENDS_LIST;

        editPage.getCancel().click();

        waitForUrlToLoad(Urls.Tools.TRENDS_LIST, Optional.empty());
        
        String actualURL = getCurrentUrl();

        assertThat(actualURL).isEqualTo(expectedURL);
    }
}
