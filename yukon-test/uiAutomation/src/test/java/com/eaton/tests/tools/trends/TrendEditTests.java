package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.TrendMarkerModal;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;

public class TrendEditTests extends SeleniumTestSetup {

    private TrendEditPage editPage;
    private DriverExtensions driverExt;
    private String trendName;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendAllFields();
        
        JSONObject response = pair.getValue1();
        
        int trendId = response.getInt("trendId");
        trendName = response.getString("name");
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void editTrend_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Trend: " + trendName;
        String actualPageTitle;

        actualPageTitle = editPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TRENDS })
    public void editTrend_AllFields_Success() {
        setRefreshPage(true);
        String editTrendName = "EditTrendTest " + timeStamp;
        
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendAllFields();
        
        JSONObject response = responses.getValue1();
        Integer editTrendId = response.getInt("trendId");

        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);
        
        editPage.getName().setInputValue(editTrendName);
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(editTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_Name_RequiredValidation() {
        setRefreshPage(true);
        editPage.getName().clearInputValue();
        editPage.getSave().click();
        
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_Name_AlreadyExistsValidation() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getName().setInputValue(trendName);
        editPage.getSave().click();
        
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name already exists");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_RemovePoint_Success() {    
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.empty());
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickActionIcon(Icons.REMOVE);
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_RemoveMarker_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendWithMarker();
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getAdditionalOptionsTab().click();
        
        WebTableRow row = editPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickActionIcon(Icons.REMOVE);
        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_AddPoint_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        TrendPointModal modal = editPage.showAndWaitAddPointModal();
        SelectPointModal pointModal = modal.showAndWaitSelectPointModal();
        String pointName = TestDbDataType.TrendPointData.CREATE_TREND_ANALOG_POINT_ID.getName();
        String pointId = TestDbDataType.TrendPointData.CREATE_TREND_ANALOG_POINT_ID.getId().toString();
        
        pointModal.selectPoint(pointName, Optional.of(pointId));
        pointModal.clickOkAndWaitForModalCloseDisplayNone();
        modal.clickOkAndWaitForModalToClose();
        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_AddMarker_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);
        
        editPage.getAdditionalOptionsTab().click();
        
        TrendMarkerModal modal = editPage.showAndWaitAddMarkerModal();
        modal.getLabel().setInputValue("Test label");
        modal.clickOkAndWaitForModalToClose();

        editPage.getSave().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_EditPoint_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> responses = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.empty());
        
        JSONObject response = responses.getValue1();
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        String pointName = TestDbDataType.TrendPointData.CREATE_TREND_ANALOG_POINT_ID.getName();
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);

        TrendPointModal modal = editPage.showAndWaitEditPointModal("Edit " + pointName, 0);
        
        modal.getLabel().setInputValue("Edit Point Label");
        modal.clickOkAndWaitForModalToClose();

        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void editTrend_EditMarker_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithMarker();
        
        JSONObject response = pair.getValue1();
        
        Integer newTrendId = response.getInt("trendId");
        String newTrendName = response.getString("name");
        
        JSONArray trendSeries = response.getJSONArray("trendSeries");
        JSONObject marker = (JSONObject)trendSeries.get(0);
        String markerLabel = marker.get("label").toString();
        
        navigate(Urls.Tools.TREND_EDIT + newTrendId + Urls.EDIT);

        editPage.getAdditionalOptionsTab().click();
        TrendMarkerModal markerModal = editPage.showAndWaitEditMarkerModal("Edit " + markerLabel, 0);        

        markerModal.getLabel().setInputValue("Edit Marker Label");
        markerModal.clickOkAndWaitForModalToClose();;

        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(newTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void editTrend_Cancel_NavigatesToCorrectUrl() {
        setRefreshPage(true);

        editPage.getCancel().click();

        Boolean loaded = waitForUrlToLoad(Urls.Tools.TRENDS_LIST, Optional.empty());
        
        assertThat(loaded).isTrue();
    }
}
