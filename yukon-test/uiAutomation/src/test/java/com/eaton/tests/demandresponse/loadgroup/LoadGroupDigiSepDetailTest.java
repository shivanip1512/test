package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder.Builder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDigiSepDetailPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupDigiSepDetailTest extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    Builder builder;
    private LoadGroupDigiSepDetailPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpDigiSepDetail_Delete_Success() {
        builder = LoadGroupDigiSepCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String expected_msg = name + " deleted successfully.";
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        detailPage = new LoadGroupDigiSepDetailPage(driverExt, id);
        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_Copy_Success() {
    	builder = LoadGroupDigiSepCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String copyName= "Copy of " + name;
        final String expected_msg = copyName + " copied successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        detailPage = new LoadGroupDigiSepDetailPage(driverExt, id);
        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();
        
        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(expected_msg);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_AssertLabels_Success() {
    	SoftAssertions softly = new SoftAssertions();
    	builder = LoadGroupDigiSepCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        detailPage = new LoadGroupDigiSepDetailPage(driverExt, id);
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
    	
        List<String> expectedLabels = new ArrayList<>(List.of("Device Class:"));
        List<String> actualLabels = detailPage.getPageSection("Device Class").getSectionLabels();
        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
        
        actualLabels = detailPage.getPageSection("Enrollment").getSectionLabels();
        expectedLabels = new ArrayList<>(List.of("Utility Enrollment Group:"));
        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);       
        
        actualLabels = detailPage.getPageSection("Timing").getSectionLabels();
        expectedLabels = new ArrayList<>(List.of("Ramp In Time:", "Ramp Out Time:"));
        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);       
       
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpDigiSepDetail_AssertValues_Success() {
    	SoftAssertions softly = new SoftAssertions();
    	builder = LoadGroupDigiSepCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        detailPage = new LoadGroupDigiSepDetailPage(driverExt, id);
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
        
        List<String> sectionValues = detailPage.getPageSection("Device Class").getSectionValues();
        
        softly.assertThat(sectionValues.size()).isEqualTo(1);
        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_DIGI_SEP.deviceClassSet[0]"));
        
        sectionValues = detailPage.getPageSection("Enrollment").getSectionValues();
        
        softly.assertThat(sectionValues.size()).isEqualTo(1);
        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_DIGI_SEP.utilityEnrollmentGroup").toString());
        
        sectionValues = detailPage.getPageSection("Timing").getSectionValues();
        
        softly.assertThat(sectionValues.size()).isEqualTo(2);
        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_DIGI_SEP.rampInMinutes").toString() + "  " + "Minutes");
        softly.assertThat(sectionValues.get(1)).isEqualTo(getResponse.path("LM_GROUP_DIGI_SEP.rampOutMinutes").toString() + "  " + "Minutes");
        
        softly.assertAll();
    }
}