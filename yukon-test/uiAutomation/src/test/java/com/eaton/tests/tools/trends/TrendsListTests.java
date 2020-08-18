package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.javatuples.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.json.JSONObject;
import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendPointBuilder;
import com.eaton.builders.tools.webtrends.TrendTypes;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.ResetPeakModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendsListPage;
import io.restassured.response.ExtractableResponse;

public class TrendsListTests extends SeleniumTestSetup {

    private TrendsListPage listPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    private String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty()).create();

        ExtractableResponse<?> response = pair.getValue1();

        trendId = response.path("trendId");
        trendName = response.path("name").toString();
        navigate(Urls.Tools.TRENDS_LIST);
        listPage = new TrendsListPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendsList_Create_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Create Trend";
        navigate(Urls.Tools.TRENDS_LIST);
        
        listPage.getActionBtn().clickAndSelectOptionByText("Create");
        String actualTitle = listPage.getPageTitle();

        assertThat(actualTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendsList_Edit_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Edit Trend: " + trendName;

        navigate(Urls.Tools.TREND_EDIT + trendId);

        listPage.getActionBtn().clickAndSelectOptionByText("Edit");

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendsList_Delete_OpensCorrectModal() {
        String expectedModalTitle = "Confirm Delete";
        
        navigate(Urls.Tools.TREND_EDIT + trendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();

        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendsList_Delete_ConfirmMessageCorrect() {
        String expectedModalMessage = "Are you sure you want to delete \"" + trendName + "\"?";

        navigate(Urls.Tools.TREND_EDIT + trendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();

        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TOOLS, TestConstants.Tools.TRENDS })
    public void trendsList_DeleteTrend_Success() {
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .create();

        ExtractableResponse<?> response = pair.getValue1();
        Integer deleteTrendId = response.path("trendId");
        String deleteTrendName = response.path("name").toString();
        
        String expectedMessage = deleteTrendName + " deleted successfully.";

        navigate(Urls.Tools.TREND_EDIT + deleteTrendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();

        TrendsListPage trendListPage = new TrendsListPage(driverExt);

        String userMsg = trendListPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);

    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendsList_ResetPeak_OpensCorrectModal() {
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(4999)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.of(TrendTypes.Type.PEAK_TYPE))
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty()).build() })
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        trendId = response.path("trendId");
        trendName = response.path("name").toString();

        navigate(Urls.Tools.TREND_EDIT + trendId);

        final String expectedModalTitle = "Reset Peak";

        ResetPeakModal ResetPeakModal = listPage.showResetPeakTrendModal();
        String actualModalTitle = ResetPeakModal.getModalTitle();
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
}