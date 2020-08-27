package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.trends.TrendCreateBuilder;
import com.eaton.builders.tools.trends.TrendMarkerBuilder;
import com.eaton.builders.tools.trends.TrendPointBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendsListPage;

import io.restassured.response.ExtractableResponse;

public class TrendsDeleteTests extends SeleniumTestSetup {

    private TrendsListPage listPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .create();

        ExtractableResponse<?> response = pair.getValue1();
        
        trendId = response.path("trendId");
        trendName = response.path("name").toString();

        navigate(Urls.Tools.TRENDS_LIST);
        listPage = new TrendsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendsDelete_DeleteConfirmMessageCorrect() {
        String expectedModalMessage = "Are you sure you want to delete \"" + trendName + "\"?";
        
        navigate(Urls.Tools.TRENDS_LIST + "/" + trendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();

        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendsDelete_AllFields_DeleteSuccessfully() {                
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withMarkers(new JSONObject[] { new TrendMarkerBuilder.Builder()
                    .withMultiplier(Optional.empty())
                    .withLabel(Optional.empty())
                    .withColor(Optional.empty())
                    .withAxis(Optional.empty())
                    .build()})
                .withPoints(new JSONObject[] {new TrendPointBuilder.Builder()
                        .withpointId(4999)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.empty())
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build()})
                .create();

        ExtractableResponse<?> response = pair.getValue1();
        
        Integer deleteTrendId = response.path("trendId");
        String deleteTrendName = response.path("name").toString();
        
        navigate(Urls.Tools.TRENDS_LIST + "/" + deleteTrendId);
        listPage = new TrendsListPage(driverExt);

        String expectedMessage = deleteTrendName + " deleted successfully.";
        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        TrendsListPage listPage = new TrendsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}