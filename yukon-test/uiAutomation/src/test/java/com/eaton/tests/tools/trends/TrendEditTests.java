package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendCreateBuilder.Builder;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icon;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.TrendAddMarkerModal;
import com.eaton.elements.modals.TrendAddPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;
import com.eaton.pages.tools.trends.TrendsDetailPage;

import io.restassured.response.ExtractableResponse;

public class TrendEditTests extends SeleniumTestSetup {

    private TrendEditPage editPage;
    private DriverExtensions driverExt;
    Builder builder;
    private int trendId;
    private String trendName;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        builder = TrendCreateBuilder.buildTrend();
        Pair<JSONObject, ExtractableResponse<?>> pair = builder.create();
        ExtractableResponse<?> response = pair.getValue1();
        trendId = response.path("trendId");
        trendName = response.path("name").toString();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage = new TrendEditPage(driverExt, Urls.Tools.TREND_EDIT, trendId);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void editTrend_PageTitleCorrect() {

        final String EXPECTED_TITLE = "Edit Trend: " + trendName;
        String actualPageTitle;

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        actualPageTitle = editPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void editTrend_AllFields_Success() {

        String editTrendName = "EditTrendTest " + timeStamp;
        Integer editTrendId;

        Pair<JSONObject, ExtractableResponse<?>> pair = builder.withName(editTrendName).create();
        ExtractableResponse<?> response = pair.getValue1();
        editTrendId = response.path("trendId");

        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);
        
        editTrendId = response.path("trendId");
        
        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);

        editPage.getName().setInputValue(editTrendName);
        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(editTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_Name_RequiredValidation() {

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage.getName().setInputValue("");
        editPage.getSave().click();
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_Name_AlreadyExistsValidation() {

        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String existingTrendName = "AT Trend " + timeStamp;

        builder.withName(existingTrendName).create();

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage.getName().setInputValue(existingTrendName);
        editPage.getSave().click();
        assertThat(editPage.getName().getValidationError()).isEqualTo("Name already exists");

    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_RemovePoint_Success() {

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.REMOVE);
        editPage.getSave().click();
        assertThat(editPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_RemoveMarker_Success() {
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        
        editPage.getAdditionalOptionsTab().click();
        
        WebTableRow row = editPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.REMOVE);
        editPage.getSave().click();
        
        
        
        assertThat(editPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_AddPoint_Success() {
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);

        TrendAddPointModal modal = editPage.showAndWaitAddPointModal();
        SelectPointModal pointModal = modal.showAndWaitAddPointModal();
        pointModal.selectPoint("Analog Point for Create Trend", Optional.of("5231"));
        pointModal.clickOkAndWaitForSpinner();
        modal.clickOkAndWaitForModalCloseDisplayNone();
        editPage.getSave().click();
        
        waitForPageToLoad(Urls.Tools.TREND_DETAILS + trendId, Optional.of(3));
        
        TrendsDetailPage detailsPage = new TrendsDetailPage(driverExt, trendId);
        
        assertThat(detailsPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_AddMarker_Success() {
        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage.getAdditionalOptionsTab().click();

        TrendAddMarkerModal modal = editPage.showAndWaitAddMarkerModal();
        modal.getLabel().setInputValue("Test label");
        modal.clickOkAndWaitForModalToClose();

        editPage.getSave().click();
        
        waitForPageToLoad(Urls.Tools.TREND_DETAILS + trendId, Optional.of(3));
        
        TrendsDetailPage detailsPage = new TrendsDetailPage(driverExt, trendId);
        
        assertThat(detailsPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_EditPoint_Success() {

        String editTrendName = "TrendEditPointTest " + timeStamp;
        Pair<JSONObject, ExtractableResponse<?>> pair = builder.withName(editTrendName).create();
        ExtractableResponse<?> response = pair.getValue1();
        int editTrendId = response.path("trendId");
        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);

        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);

        TrendAddPointModal modal = new TrendAddPointModal(this.driverExt, Optional.of("Edit Month History"), Optional.empty());
        modal.getLabel().setInputValue("Edit Point Label");
        modal.clickOkAndWaitForModalCloseDisplayNone();

        editPage.getSave().click();
        
        waitForPageToLoad(Urls.Tools.TREND_DETAILS + trendId, Optional.of(3));
        
        TrendsDetailPage detailsPage = new TrendsDetailPage(driverExt, trendId);
        
        assertThat(detailsPage.getUserMessage()).isEqualTo(editTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void editTrend_EditMarker_Success() {
        String editTrendName = "TrendEditMarkerTest " + timeStamp;

        Pair<JSONObject, ExtractableResponse<?>> pair = builder.withName(editTrendName).create();
        ExtractableResponse<?> response = pair.getValue1();
        int editTrendId = response.path("trendId");
        String markerLabel = response.path("trendSeries[0].label");
        navigate(Urls.Tools.TREND_EDIT + editTrendId + Urls.EDIT);

        editPage.getAdditionalOptionsTab().click();
        WebTableRow row = editPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);

        TrendAddMarkerModal modal = new TrendAddMarkerModal(this.driverExt, Optional.of("Edit " + markerLabel), Optional.empty());
        modal.getLabel().setInputValue("Edit Marker Label");
        modal.clickOkAndWaitForModalToClose();
        editPage.getSave().click();
        
        waitForPageToLoad(Urls.Tools.TREND_DETAILS + trendId, Optional.of(3));
        
        TrendsDetailPage detailsPage = new TrendsDetailPage(driverExt, trendId);
        
        assertThat(detailsPage.getUserMessage()).isEqualTo(editTrendName + " saved successfully.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void editTrend_Cancel_NavigatesToCorrectUrl() {
        String expectedURL = getBaseUrl() + Urls.Tools.TRENDS_LIST;
        String actualURL;

        navigate(Urls.Tools.TREND_EDIT + trendId + Urls.EDIT);
        editPage.getCancel().click();
        
        waitForPageToLoad(Urls.Tools.TREND_DETAILS + trendId, Optional.of(3));
        
        actualURL = getCurrentUrl();

        assertThat(actualURL).isEqualTo(expectedURL);
    }
}
