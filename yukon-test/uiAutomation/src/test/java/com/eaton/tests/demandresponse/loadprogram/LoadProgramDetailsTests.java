package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateService;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class LoadProgramDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    LoadProgramDetailPage detailPage;
    private String ldPgmName;
    private double triggerOffset;
    private double restoreOffset;
    private String gearName;
    private String groupName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Map<String, Pair<JSONObject, JSONObject>> pair = LoadProgramCreateService.createEcobeeProgramAllFieldsWithSetPointGear();
        
        Pair<JSONObject, JSONObject> programPair = pair.get("LoadProgram");
        Pair<JSONObject, JSONObject> loadGroupPair = pair.get("LoadGroup");
        
        JSONObject request = programPair.getValue0();
        JSONObject response = programPair.getValue1();
        Integer ldPgmId = response.getInt("programId");
        
        JSONObject groupResponse = loadGroupPair.getValue1();
        groupName = groupResponse.getString("name");
        
        ldPgmName = request.getString("name");
        triggerOffset = request.getDouble("triggerOffset");
        restoreOffset = request.getDouble("restoreOffset");
        org.json.JSONArray gears = request.getJSONArray("gears");
        JSONObject gear = (JSONObject) gears.get(0);
        gearName = gear.getString("gearName");

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + ldPgmId);
        detailPage = new LoadProgramDetailPage(driverExt, ldPgmId);
    }
    
    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmDetail_Page_TitleCorrect() {
        setRefreshPage(false);
        final String EXPECTED_TITLE = "Load Program: " + ldPgmName;

        String actualPageTitle = detailPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GeneralSection_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getGeneralSection().getSectionLabels();
        
        softly.assertThat(4).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Operational State:").isEqualTo(labels.get(2));
        softly.assertThat("Constraint:").isEqualTo(labels.get(3));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GeneralSection_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getGeneralSection().getSectionValues();
        
        softly.assertThat(4).isEqualTo(values.size());
        softly.assertThat(ldPgmName).isEqualTo(values.get(0));
        softly.assertThat("LM ecobee Program").isEqualTo(values.get(1));
        softly.assertThat("Manual Only").isEqualTo(values.get(2));
        softly.assertThat("Default Constraint").isEqualTo(values.get(3));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_TriggerThresholdSettingsSection_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getTrgThresholdSection().getSectionLabels();
        
        softly.assertThat(2).isEqualTo(labels.size());
        softly.assertThat("Trigger Offset:").isEqualTo(labels.get(0));
        softly.assertThat("Restore Offset:").isEqualTo(labels.get(1));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_TriggerThresholdSettingsSection_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTrgThresholdSection().getSectionValues();
        
        softly.assertThat(2).isEqualTo(values.size());
        softly.assertThat(Double.toString(triggerOffset)).isEqualTo(values.get(0));
        softly.assertThat(Double.toString(restoreOffset)).isEqualTo(values.get(1));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_ControlWindowSection_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getControlWindowSection().getSectionLabels();
        
        softly.assertThat(6).isEqualTo(labels.size());
        softly.assertThat("Use Window 1:").isEqualTo(labels.get(0));
        softly.assertThat("Start Time:").isEqualTo(labels.get(1));
        softly.assertThat("Stop Time:").isEqualTo(labels.get(2));
        softly.assertThat("Use Window 2:").isEqualTo(labels.get(3));
        softly.assertThat("Start Time:").isEqualTo(labels.get(4));
        softly.assertThat("Stop Time:").isEqualTo(labels.get(5));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_ControlWindowSection_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getControlWindowSection().getSectionValues();

        softly.assertThat(6).isEqualTo(values.size());
        softly.assertThat("Yes").isEqualTo(values.get(0));
        softly.assertThat("01:00").isEqualTo(values.get(1));
        softly.assertThat("01:00").isEqualTo(values.get(2));
        softly.assertThat("Yes").isEqualTo(values.get(3));
        softly.assertThat("01:00").isEqualTo(values.get(4));
        softly.assertThat("01:00").isEqualTo(values.get(5));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GearsSection_LablelsCorrect() {
        setRefreshPage(false);
        List<String> expectedLabels= new ArrayList<>(List.of("Name", "Type"));
        List<String> actualLabels = detailPage.getGearsSectionTable().getListTableHeaders();
        assertThat(actualLabels).isEqualTo(expectedLabels);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GearsSection_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();

        WebTableRow row = detailPage.getGearsSectionTable().getDataRowByIndex(0);
        
        String name = row.getCellLinkTextByIndex(0);
        String type = row.getCellTextByNthChild(2);
        
        softly.assertThat(name).isEqualTo(gearName);
        softly.assertThat(type).isEqualTo("ecobee Setpoint");
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_Tab_TitlesCorrect() {
        setRefreshPage(false);
        List<String> expectedTabTitles= new ArrayList<>(List.of("Load Groups", "Member Control", "Notification"));
        List<String> actualTabTitles = detailPage.getAllTabs().getTitles();
        
        assertThat(actualTabTitles).isEqualTo(expectedTabTitles);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_LoadGroup_HeadersCorrect() {
        setRefreshPage(false);
        
        List<String> expectedTabTitles= new ArrayList<>(List.of("Name", "Type"));
        List<String> actualHeaders = detailPage.getLoadGroupTable().getListTableHeaders();
        
        assertThat(actualHeaders).isEqualTo(expectedTabTitles);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_LoadGroup_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        
        WebTableRow row = detailPage.getLoadGroupTable().getDataRowByIndex(0);
        
        String name = row.getCellLinkTextByIndex(0);
        String type = row.getCellTextByNthChild(2);
        
        softly.assertThat(name).isEqualTo(groupName);
        softly.assertThat(type).isEqualTo("ecobee Group");
        softly.assertAll();
    }
    
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_Create_UrlCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        
        final String createUrl = getBaseUrl() + Urls.DemandResponse.LOAD_PROGRAM_CREATE;

        String actualOtherActionUrl = detailPage.getActionBtn().getOptionLinkByText("Create");
        
        ExtractableResponse<?> response = ApiCallHelper.get(createUrl);

        softly.assertThat(actualOtherActionUrl).isEqualTo(createUrl);
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_Delete_InfoMsgCorrect() {
        setRefreshPage(true);
           
        ConfirmModal deleteModal = detailPage.showDeleteLoadProgramModal();
        String msg = deleteModal.getWarningMsg();
        
        assertThat(msg).isEqualTo("Deleting this program will remove it from any control area or scenario it is currently assigned to. The program’s control history will be deleted.");
    }
}
