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

import com.eaton.builders.tools.trends.TrendCreateService;
import com.eaton.builders.tools.trends.TrendTypes;
import com.eaton.elements.modals.ResetPeakModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendsDetailPage;

public class TrendResetPeakTest extends SeleniumTestSetup {

    private TrendsDetailPage detailsPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.of(TrendTypes.Type.PEAK_TYPE));

        JSONObject response = pair.getValue1();

        trendId = response.getInt("trendId");
        trendName = response.getString("name");

        navigate(Urls.Tools.TREND_DETAILS + trendId);
        detailsPage = new TrendsDetailPage(driverExt, trendId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(detailsPage);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendResetPeak_Field_LabelsCorrect() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        List<String> expectedLabels = new ArrayList<>(List.of("Reset Peak To:", "Date:", "Reset Peak For All Trends:"));
        List<String> actualLabels = resetPeakModal.getFieldLabels();

        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendResetPeak_DefaultFields_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();

        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        softly.assertThat(resetPeakModal.getResetPeakTo().getSelectedValue()).isEqualTo("Today");
        softly.assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
        softly.assertThat(resetPeakModal.getDate().getValue()).isEqualTo(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        softly.assertThat(resetPeakModal.getResetPeakForAllTrends().getValueChecked()).isEqualTo("false");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_NoPoint_ResetPeakDisabled() {
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendOnlyRequiredFields();

        JSONObject response = pair.getValue1();

        Integer trendIdTypeNotPeak = response.getInt("trendId");

        navigate(Urls.Tools.TREND_DETAILS + trendIdTypeNotPeak);

        detailsPage.getActionBtn().clickAndWait();

        assertThat(detailsPage.getActionBtn().isActionEnabled("Reset Peak")).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakForAllTrendsYes_Success() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = TrendCreateService.buildAndCreateTrendWithPoint(Optional.empty(), Optional.of(TrendTypes.Type.PEAK_TYPE));

        JSONObject response = pair.getValue1();

        Integer id = response.getInt("trendId");

        navigate(Urls.Tools.TREND_DETAILS + id);
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakForAllTrends().selectByValue("Yes");

        resetPeakModal.clickOkAndWaitForModalToClose();        
        String actualUserMessage = detailsPage.getResetPeakMessage();
        softly.assertThat(actualUserMessage).contains("Reset peak performed successfully for ");
        softly.assertThat(actualUserMessage).contains(trendName);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakForAllTrendsNo_Success() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakForAllTrends().selectByValue("No");
        resetPeakModal.clickOkAndWaitForModalToClose();

        assertThat(detailsPage.getUserMessage()).contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakTo_ValuesCorrect() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        List<String> expectedDropDownValues = new ArrayList<>(List.of("Today", "First Date of Month", "First Date of Year", "Selected Date"));
        List<String> actualDropDownValues = resetPeakModal.getResetPeakTo().getOptionValues();

        assertThat(expectedDropDownValues).containsExactlyElementsOf(actualDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakToSelectedDate_DateEnabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByValue("SELECTED_DATE");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakToFirstMonthDate_DateDisabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByValue("FIRST_DATE_OF_MONTH");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendResetPeak_ResetPeakToFirstYearDate_DateDisabled() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByValue("FIRST_DATE_OF_YEAR");

        assertThat(resetPeakModal.getDate().isPickerEnabled()).isFalse();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.TRENDS })
    public void trendResetPeak_SelectedDate_Success() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.getResetPeakTo().selectItemByValue("SELECTED_DATE");

        resetPeakModal.getDate().setValue(LocalDate.now().minusDays(10).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        resetPeakModal.clickOkAndWaitForModalToClose();

        assertThat(detailsPage.getResetPeakMessage()).contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.TRENDS })
    public void trendResetPeak_Help_TextMessageCorrect() {
        SoftAssertions softly = new SoftAssertions();
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.clickHelpIconAndWait();

        String actualTextMessage = resetPeakModal.getHelpTextMessage();
        String expectedTextSection1 = "The Peak type is used to display data for the peak date. When a Peak type is added, the minimum starting date is set to the first day of this month and will remain at this date until Reset Peaks is performed. The peak day is determined by finding the highest archived value from the minimum starting date through today. The data from the peak day is overlaid and repeated for every day displayed in the trend.";
        String expectedTextSection2 = "Use Reset Peaks to change the minimum starting date to: Today, First Date of Month, First Date of Year, Selected Date.";
        String expectedTextSection3 = "Reset Peaks For All Trends: Choose Yes to reset the minimum starting date for all Trends in the system.";

        softly.assertThat(actualTextMessage).contains(expectedTextSection1);
        softly.assertThat(actualTextMessage).contains(expectedTextSection2);
        softly.assertThat(actualTextMessage).contains(expectedTextSection3);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void trendResetPeak_Close_HelpMessageSuccess() {
        ResetPeakModal resetPeakModal = detailsPage.showResetPeakTrendModal();

        resetPeakModal.clickHelpIconAndWait();
        resetPeakModal.clickHelpCloseIcon();

        assertThat(resetPeakModal.isHelpClosed()).isTrue();
    }
}