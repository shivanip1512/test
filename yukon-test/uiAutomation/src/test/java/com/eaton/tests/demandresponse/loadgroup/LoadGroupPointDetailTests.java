package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.PointId;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupPointDetailsPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupPointDetailTests extends SeleniumTestSetup {
	private DriverExtensions driverExt;
    private LoadGroupPointDetailsPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup()
				.create();
        
        response = pair.getValue1();
        int id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupPointDetailsPage(driverExt, id);
    }
    
    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }
	 
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpPointDetail_Delete_Success() {
    	setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String createName = "AT Create Point Ld group " + timeStamp;
        Pair<JSONObject, JSONObject> pair = new LoadGroupPointCreateBuilder.Builder(Optional.empty())
                .withName(createName)
                .withPointUsageId(Optional.of(PointId.CAPACITOR_BANK_STATE))
                .withDeviceUsageId(Optional.empty())
                .withPointStartControlRawState(Optional.of(LoadGroupEnums.PointStartControlRawState.FALSE))
                .withKwCapacity(Optional.of(67.0))
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(false))
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

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpPointDetail_Copy_Success() {
    	setRefreshPage(true);
    	Pair<JSONObject, JSONObject> pair = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup().create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");
        String name = response.getString("name");
        final String copyName = "Copy of " + name;
        final String expected_msg = copyName + " copied successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpPointDetail_PointGroup_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getPointGroupSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(2);
                
        softly.assertThat("Control Device Point:").isEqualTo(labels.get(0));
        softly.assertThat("Control Start State:").isEqualTo(labels.get(1));
        
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpPointDetail_PointGroup_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup()
				.create();
        response = pair.getValue1();
        int id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        List<String> values = detailPage.getPointGroupSection().getSectionValues();
        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);

        softly.assertThat(values.size()).isEqualTo(2);
        softly.assertThat(values.get(0)).isEqualTo(getResponse.path("LM_GROUP_POINT.deviceUsage.name").toString() + ": "
                + getResponse.path("LM_GROUP_POINT.pointUsage.name").toString());
        softly.assertThat(values.get(1))
                .isEqualTo("  " + getResponse.path("LM_GROUP_POINT.startControlRawState.stateText").toString());

        softly.assertAll();
    }
}