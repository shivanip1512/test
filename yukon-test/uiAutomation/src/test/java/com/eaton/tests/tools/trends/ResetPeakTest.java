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

    private TrendsDetailPage detailPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        softly = new SoftAssertions();
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

        navigate(Urls.Tools.TRENDS_DETAIL + trendId);
        waitForPageToLoad(trendName, Optional.empty());
        detailPage = new TrendsDetailPage(driverExt, trendId);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(detailPage);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_FieldLabelsCorrect() {

        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));
        List<String> expectedLabels = new ArrayList<>(List.of("Reset Peak To:", "Date:", "Reset Peak For All Trends:"));
        List<String> actualLabels = resetPeakModal.getFieldLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_FieldValuesCorrect() {

        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        softly.assertThat(resetPeakModal.getResetPeakTo().getSelectedValue()).isEqualTo("Today");
        softly.assertThat(resetPeakModal.getDate().getNumericPicker().getAttribute("disabled"))
                .isEqualTo("true");
        softly.assertThat(resetPeakModal.getDate().getNumericPicker().getAttribute("value"))
                .isEqualTo(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        softly.assertThat(resetPeakModal.getResetPeakForAllTrends().getValueChecked()).isEqualTo("false");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_TypeNotPeak_ResetPeakDisabled() {

        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        Integer trendIdTypeNotPeak = response.path("trendId");

        navigate(Urls.Tools.TRENDS_DETAIL + trendIdTypeNotPeak);
        assertThat(detailPage.getActionBtn().checkOptionIsEnabledByText("Reset Peak")).isFalse();

        navigate(Urls.Tools.TRENDS_DETAIL + trendId);
        waitForPageToLoad(trendName, Optional.empty());
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeak_ForAllTrendsSuccess() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakForAllTrends().setByValue("true", true);
        detailPage.getUserMessage().contains("Reset peak performed successfully for " + trendName + ",");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeak_NotAllTrendsSuccess() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakForAllTrends().setByValue("false", true);
        resetPeakModal.clickOkAndWait();
        detailPage.getUserMessage().contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakTo_DropDownContainsAllExpectedValues() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        List<String> expectedDropDownValues = new ArrayList<>(
                List.of("Today", "First Date of Month", "First Date of Year", "Selected Date"));
        List<String> actualDropDownValues = resetPeakModal.getResetPeakTo().getOptionValues();

        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToSelectedDate_DateEnabled() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakTo().selectItemByText("Selected Date");

        String disabled = resetPeakModal.getDate().getNumericPicker().getAttribute("disabled");
        assertThat(disabled == null).isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToFirstMonth_DateDisabled() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakTo().selectItemByText("First Date of Month");

        String disabled = resetPeakModal.getDate().getNumericPicker().getAttribute("disabled");
        assertThat(disabled.equals("true")).isTrue();
        String actualDate = resetPeakModal.getDate().getNumericPicker().getAttribute("value");
        String expectedDate = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakToFirstYear_DateDisabled() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakTo().selectItemByText("First Date of Year");

        String disabled = resetPeakModal.getDate().getNumericPicker().getAttribute("disabled");
        assertThat(disabled.equals("true")).isTrue();
        String actualDate = resetPeakModal.getDate().getNumericPicker().getAttribute("value");
        String expectedDate = LocalDate.now().withDayOfYear(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Tools.TRENDS })
    public void trendResetPeak_SetResetPeakSelectedDate_SetDateSaveSuccess() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.getResetPeakTo().selectItemByText("Selected Date");

        resetPeakModal.getDate().setValue(LocalDate.now().minusDays(10).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        resetPeakModal.clickOkAndWaitForModalToClose();
        detailPage.getUserMessage().contains("Reset peak performed successfully for " + trendName + ".");
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakModal_HelpTextMessage() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.clickHelpIcon();

        String actualTextMessage = resetPeakModal.getHelpTextMessage();
        String expectedTextMessage1 = "The Peak type is used to display data for the peak date. When a Peak type is added, the minimum starting date is set to the first day of this month and will remain at this date until Reset Peaks is performed. The peak day is determined by finding the highest archived value from the minimum starting date through today. The data from the peak day is overlaid and repeated for every day displayed in the trend.";
        String expectedTextMessage2 = "Use Reset Peaks to change the minimum starting date to: Today, First Date of Month, First Date of Year, Selected Date.";
        String expectedTextMessage3 = "Reset Peaks For All Trends: Choose Yes to reset the minimum starting date for all Trends in the system.";
        softly.assertThat(actualTextMessage).contains(expectedTextMessage1);
        softly.assertThat(actualTextMessage).contains(expectedTextMessage2);
        softly.assertThat(actualTextMessage).contains(expectedTextMessage3);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendResetPeak_ResetPeakModal_HelpCloseIcon_Click() {
        detailPage.getActionBtn().clickAndSelectOptionByText("Reset Peak");
        waitForLoadingSpinner();
        ResetPeakModal resetPeakModal = new ResetPeakModal(driverExt, Optional.of("Reset Peak"), Optional.of("ui-id-3"));

        resetPeakModal.clickHelpIcon();
        resetPeakModal.clickHelpCloseIcon();

        assertThat(resetPeakModal.helpTextMessageClosed()).isTrue();
    }
}