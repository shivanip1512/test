package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.gears.EcobeeSetpointGearBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.OperationalState;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.ProgramType;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadProgramDetailPage;

public class LoadProgramDetailsTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    LoadProgramDetailPage detailPage;
    private String ldGrpName;
    private String ldPgmName;
    private int ldGrpId;
    private int ldPgmId;
    String timeStamp;
    private JSONObject ldPgm;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        ldGrpName = "LdGroupForPgmDetail " + timeStamp;
        ldPgmName = "LdPgmForPgmDetail " + timeStamp;
        Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of(ldGrpName))
                .create();
        JSONObject ldGrp = pair.getValue1();
        ldGrpId = ldGrp.getInt("id");
        JSONObject gear = EcobeeSetpointGearBuilder.gearBuilder().withName("TestGear")
                .build();

        Pair<JSONObject, JSONObject> pair2 = LoadProgramCreateBuilder
                .buildLoadProgram(ProgramType.ECOBEE_PROGRAM, new ArrayList<>(
                        List.of(gear)),
                        new ArrayList<>(
                                List.of(ldGrpId)))
                .withName(Optional.of(ldPgmName))
                .withOperationalState(Optional.of(OperationalState.Manual_Only))
                .withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
                .withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
                .withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
                .withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
                .create();
        
        JSONObject response = pair2.getValue1();
        ldPgm = pair2.getValue0();
        ldPgmId = response.getInt("programId");

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + ldPgmId);
        detailPage = new LoadProgramDetailPage(driverExt, ldPgmId);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmDetail_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Load Program: " + ldPgmName;

        String actualPageTitle = detailPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GeneralSection_LabelsCorrect() {
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
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getGeneralSection().getSectionValues();
        
        softly.assertThat(4).isEqualTo(values.size());
        softly.assertThat(ldPgm.get("name")).isEqualTo(values.get(0));
        softly.assertThat("LM ecobee Program").isEqualTo(values.get(1));
        softly.assertThat("Manual Only").isEqualTo(values.get(2));
        softly.assertThat("Default Constraint").isEqualTo(values.get(3));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_TriggerThresholdSettingsSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getTrgThresholdSection().getSectionLabels();
        
        softly.assertThat(2).isEqualTo(labels.size());
        softly.assertThat("Trigger Offset:").isEqualTo(labels.get(0));
        softly.assertThat("Restore Offset:").isEqualTo(labels.get(1));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_TriggerThresholdSettingsSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTrgThresholdSection().getSectionValues();
        
        softly.assertThat(2).isEqualTo(values.size());
        softly.assertThat(ldPgm.get("triggerOffset").toString()).isEqualTo(values.get(0));
        softly.assertThat(ldPgm.get("restoreOffset").toString()).isEqualTo(values.get(1));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_ControlWindowSection_LabelsCorrect() {
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
        List<String> expectedLabels= new ArrayList<>(List.of("Name", "Type"));
        List<String> actualLabels = detailPage.getGearsSection().getSectionHeaders();
        assertThat(actualLabels).isEqualTo(expectedLabels);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_GearsSection_ValuesCorrect() {
        List<String> expectedLabels= new ArrayList<>(List.of("Name", "Type"));
        List<String> actualLabels = detailPage.getGearsSection().getSectionHeaders();
        assertThat(actualLabels).isEqualTo(expectedLabels);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldPrgmDetail_Tab_TitlesCorrect() {
        List<String> expectedTabTitles= new ArrayList<>(List.of("Load Groups", "Member Control", "Notification"));
        List<String> actualTabTitles = detailPage.getAllTabs().getTitles();
        
        assertThat(actualTabTitles).isEqualTo(expectedTabTitles);
    }
}
