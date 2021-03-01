package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.javatuples.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.json.JSONObject;

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.builders.tools.trends.TrendTypes;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.ResetPeakModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendsListPage;

public class TrendsListTests extends SeleniumTestSetup {

    private TrendsListPage listPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    private String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendAllFields();

        JSONObject response = pair.getValue1();

        trendId = response.getInt("trendId");
        trendName = response.getString("name");

        navigate(Urls.Tools.TRENDS_LIST);
        listPage = new TrendsListPage(driverExt);
    }    

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TRENDS })
    public void trendsList_Create_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Create Trend";

        navigate(Urls.Tools.TRENDS_LIST);
        listPage.getActionBtn().clickAndSelectOptionByText("Create");
        String actualTitle = listPage.getPageTitle();

        assertThat(actualTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TRENDS })
    public void trendsList_Edit_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Edit Trend: " + trendName;

        navigate(Urls.Tools.TREND_DETAILS + trendId);

        listPage.getActionBtn().clickAndSelectOptionByText("Edit");

        String actualPageTitle = listPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TRENDS })
    public void trendsList_Delete_OpensCorrectModal() {
        String expectedModalTitle = "Confirm Delete";

        navigate(Urls.Tools.TREND_DETAILS + trendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();

        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendsList_Delete_ConfirmMessageCorrect() {
        String expectedModalMessage = "Are you sure you want to delete \"" + trendName + "\"?";

        navigate(Urls.Tools.TREND_DETAILS + trendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();

        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TOOLS, TestConstants.Features.TRENDS })
    public void trendsList_Delete_Success() {
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendAllFields();

        JSONObject response = pair.getValue1();
        Integer deleteTrendId = response.getInt("trendId");
        String deleteTrendName = response.getString("name");

        String expectedMessage = deleteTrendName + " deleted successfully.";

        navigate(Urls.Tools.TREND_DETAILS + deleteTrendId);

        ConfirmModal deleteConfirmModal = listPage.showDeleteTrendModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();

        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendsList_ResetPeak_OpensCorrectModal() {
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.of(TrendTypes.Type.PEAK_TYPE));

        JSONObject response = pair.getValue1();

        Integer resettrendId = response.getInt("trendId");

        navigate(Urls.Tools.TREND_DETAILS + resettrendId);

        final String expectedModalTitle = "Reset Peak";

        ResetPeakModal ResetPeakModal = listPage.showResetPeakTrendModal();
        String actualModalTitle = ResetPeakModal.getModalTitle();
        
        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
}