package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendPointBuilder;
import com.eaton.builders.tools.webtrends.TrendTypes;
import com.eaton.elements.modals.ResetPeakModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendsDetailPage;

import io.restassured.response.ExtractableResponse;

public class ResetPeakTest extends SeleniumTestSetup {

    private TrendsDetailPage detailsPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(4999)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.of(TrendTypes.Type.PEAK_TYPE))
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build() })
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        trendId = response.path("trendId");
        trendName = response.path("name").toString();

        navigate(Urls.Tools.TREND_DETAILS + trendId);
        detailsPage = new TrendsDetailPage(driverExt, trendId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(detailsPage);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_FieldLabelsCorrect() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        List<String> expectedLabels = new ArrayList<>(List.of("Reset Peak To:", "Date:", "Reset Peak For All Trends:"));
        List<String> actualLabels = resetPeakModal.getFieldLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_Default_FieldValuesCorrect() {
        softly = new SoftAssertions();

        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        softly.assertThat(resetPeakModal.getResetPeakTo().getSelectedValue()).isEqualTo("Today");
        softly.assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
        softly.assertThat(resetPeakModal.getDate().getValue()).isEqualTo(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        softly.assertThat(resetPeakModal.getResetPeakForAllTrends().getValueChecked()).isEqualTo("false");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_TypeNotPeak_ResetPeakDisabled() {
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        Integer trendIdTypeNotPeak = response.path("trendId");

        navigate(Urls.Tools.TREND_DETAILS + trendIdTypeNotPeak);

        detailsPage.getActionBtn().click();

        assertThat(detailsPage.getActionBtn().isActionEnabled("Reset Peak")).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeak_ForAllTrendsSuccess() {
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] { new TrendPointBuilder.Builder()
                        .withpointId(5157)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.of(TrendTypes.Type.PEAK_TYPE))
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build() })
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        Integer id = response.path("trendId");

        navigate(Urls.Tools.TREND_DETAILS + id);
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakForAllTrends().setByValue("true", true);

        resetPeakModal.clickOkAndWaitForModalToClose();        
        String actualUserMessage = detailsPage.getResetPeakMessage();
        softly.assertThat(actualUserMessage).contains("Reset peak performed successfully for ");
        softly.assertThat(actualUserMessage).contains(trendName);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeak_NotAllTrends_Success() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakForAllTrends().setByValue("false", true);
        resetPeakModal.clickOkAndWaitForModalToClose();

        assertThat(detailsPage.getUserMessage()).contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakTo_ContainsAllExpectedValues() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("Today", "First Date of Month", "First Date of Year", "Selected Date"));
        List<String> actualDropDownValues = resetPeakModal.getResetPeakTo().getOptionValues();

        assertThat(expectedDropDownValues).containsExactlyElementsOf(actualDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToSelectedDate_DateEnabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByText("Selected Date");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToFirstMonth_DateDisabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByText("First Date of Month");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToFirstYear_DateDisabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByText("First Date of Year");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakSelectedDate_SetDateSaveSuccess() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByText("Selected Date");

        resetPeakModal.getDate().setValue(LocalDate.now().minusDays(10).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        resetPeakModal.clickOkAndWaitForModalToClose();

        assertThat(detailsPage.getResetPeakMessage()).contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakModal_HelpTextMessage() {
        softly = new SoftAssertions();
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.clickHelpIcon();

        String actualTextMessage = resetPeakModal.getHelpTextMessage();
        String expectedTextSection1 = "The Peak type is used to display data for the peak date. When a Peak type is added, the minimum starting date is set to the first day of this month and will remain at this date until Reset Peaks is performed. The peak day is determined by finding the highest archived value from the minimum starting date through today. The data from the peak day is overlaid and repeated for every day displayed in the trend.";
        String expectedTextSection2 = "Use Reset Peaks to change the minimum starting date to: Today, First Date of Month, First Date of Year, Selected Date.";
        String expectedTextSection3 = "Reset Peaks For All Trends: Choose Yes to reset the minimum starting date for all Trends in the system.";

        softly.assertThat(actualTextMessage).contains(expectedTextSection1);
        softly.assertThat(actualTextMessage).contains(expectedTextSection2);
        softly.assertThat(actualTextMessage).contains(expectedTextSection3);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakModal_HelpCloseIcon_Click() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.clickHelpIcon();
        resetPeakModal.clickHelpCloseIcon();

        assertThat(resetPeakModal.isHelpClosed()).isTrue();
    }
}