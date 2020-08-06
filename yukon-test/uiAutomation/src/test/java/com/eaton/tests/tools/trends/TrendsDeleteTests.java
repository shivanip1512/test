package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.tools.webtrends.WebTrendCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.trends.TrendsDetailPage;
import com.eaton.pages.trends.TrendsListPage;

public class TrendsDeleteTests extends SeleniumTestSetup {

    private TrendsDetailPage detailPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = new WebTrendCreateBuilder.Builder(Optional.empty())
                .withAxis(Optional.empty())
                .withColor(Optional.empty())
                .withDate(Optional.empty())
                .withLabel(Optional.empty())
                .withMultiplier(Optional.empty())
                .withPointId(Optional.empty())
                .withType(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();

        trendName = response.getString("name");
        trendId = response.getInt("trendId");

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Tools.TRENDS_DETAIL + trendId);
        detailPage = new TrendsDetailPage(driverExt, trendId);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_DeleteConfirmMessageCorrect() {
        String expectedModalMessage = "Are you sure you want to delete \"" + trendName + "\"?";

        ConfirmModal deleteConfirmModal = detailPage.showDeleteTrendModal();

        String actualModalMessage = deleteConfirmModal.getConfirmMsg();

        assertThat(actualModalMessage).isEqualTo(expectedModalMessage);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void commChannelDeleteTcp_DeleteSuccessfully() {
        Pair<JSONObject, JSONObject> pair = new WebTrendCreateBuilder.Builder(Optional.empty())
                .withAxis(Optional.empty())
                .withColor(Optional.empty())
                .withDate(Optional.empty())
                .withLabel(Optional.empty())
                .withMultiplier(Optional.empty())
                .withPointId(Optional.empty())
                .withType(Optional.empty())
                .create();

        JSONObject response = pair.getValue1();

        String deleteTrendName = response.getString("name");
        Integer deleteTrendId = response.getInt("trendId");
        navigate(Urls.Tools.TRENDS_DETAIL + deleteTrendId);
        detailPage = new TrendsDetailPage(driverExt, deleteTrendId);

        String expectedMessage = deleteTrendName + " deleted successfully.";
        ConfirmModal deleteConfirmModal = detailPage.showDeleteTrendModal();
        deleteConfirmModal.clickOkAndWaitForModalToClose();
        TrendsListPage listPage = new TrendsListPage(driverExt);
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}