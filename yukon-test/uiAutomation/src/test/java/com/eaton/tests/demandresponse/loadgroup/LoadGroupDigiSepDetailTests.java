package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDigiSepDetailPage;

public class LoadGroupDigiSepDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupDigiSepDetailPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = LoadGroupDigiSepCreateBuilder.buildLoadGroup()
                .create();
        
        response = pair.getValue1();
        int id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupDigiSepDetailPage(driverExt, id);
    }
    
    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_Delete_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupDigiSepCreateBuilder.buildLoadGroup()
                .create();        
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");
        String name = response.getString("name");
        final String expected_msg = name + " deleted successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }    
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDetail_Copy_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        final String copyName= "Copied DigiSep " + timeStamp;
        final String expected_msg = copyName + " copied successfully.";
        
        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();
        
        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(expected_msg);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_DeviceClassSection_Displayed() {
        assertThat(detailPage.getDeviceClassSection().getSection()).isNotNull();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_EnrollmentSection_Displayed() {
        assertThat(detailPage.getEnrollmentSection().getSection()).isNotNull();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_TimingsSection_Displayed() {
        assertThat(detailPage.getTimingSection().getSection()).isNotNull();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_DeviceClassSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getDeviceClassSection().getSectionLabels();
        
        softly.assertThat(1).isEqualTo(labels.size());
        softly.assertThat("Device Class:").isEqualTo(labels.get(0));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_DeviceClassSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getDeviceClassSection().getSectionValues();
        
        JSONArray devices = (JSONArray) response.get("deviceClassSet");
        
        LoadGroupEnums.DigiSepDeviceClassUIEnum deviceClass = LoadGroupEnums.DigiSepDeviceClassUIEnum.valueOf(devices.getString(0));
        
        softly.assertThat(1).isEqualTo(values.size());
        softly.assertThat(deviceClass.getDeviceClassSet()).isEqualTo(values.get(0));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_EnrollmentSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getEnrollmentSection().getSectionLabels();
        
        softly.assertThat(1).isEqualTo(labels.size());
        softly.assertThat("Utility Enrollment Group:").isEqualTo(labels.get(0));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_EnrollmentSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getEnrollmentSection().getSectionValues();
        
        softly.assertThat(1).isEqualTo(values.size());
        softly.assertThat(String.valueOf(response.getInt("utilityEnrollmentGroup"))).isEqualTo(values.get(0).toString());
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_TimingSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getTimingSection().getSectionLabels();
        
        softly.assertThat(2).isEqualTo(labels.size());
        softly.assertThat("Ramp In Time:").isEqualTo(labels.get(0));
        softly.assertThat("Ramp Out Time:").isEqualTo(labels.get(1));
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_TimingSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getTimingSection().getSectionValues();
        
        String rampIn = String.valueOf(response.getInt("rampInMinutes"));
        String rampOut = String.valueOf(response.getInt("rampOutMinutes"));
        
        softly.assertThat(2).isEqualTo(values.size());
        softly.assertThat(values.get(0)).contains(rampIn.trim());
        softly.assertThat(values.get(1)).contains(rampOut.trim());
        softly.assertAll();
    }        
}