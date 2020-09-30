package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMCTCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupMCTDetailsPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupMCTDetailTest extends SeleniumTestSetup {
	 	private DriverExtensions driverExt;
	    private LoadGroupMCTDetailsPage detailPage;
	    private JSONObject response;
	    private int id;

	    @BeforeClass(alwaysRun = true)
	    public void beforeClass() {
	        driverExt = getDriverExt();
	        setRefreshPage(false);
	        
	        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
										                .withCommunicationRoute(28)
										                .withDisableControl(Optional.of(true))
										                .withDisableGroup(Optional.of(true))
										                .withKwCapacity(Optional.empty())
										                .withAddress(34567)
										                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
										                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2))
										                .create();
	        
	        response = pair.getValue1();
	        id = response.getInt("id");
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        detailPage = new LoadGroupMCTDetailsPage(driverExt, id);
	    }
	    
	    @AfterMethod
	    public void afterMethod() {
	        if(getRefreshPage()) {
	            refreshPage(detailPage);    
	        }
	        setRefreshPage(false);
	    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpMCTDetail_Delete_Success() {
    	setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(28)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withAddress(34567)
                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2, LoadGroupEnums.RelayUsage.RELAY_1))
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
    public void ldGrpMCTDetail_Copy_Success() {
    	setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(28)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withAddress(34567)
                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2, LoadGroupEnums.RelayUsage.RELAY_1))
                .create();
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
    public void ldGrpMCTDetail_GeneralSection_LabelsCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(28)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withAddress(34567)
                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2, LoadGroupEnums.RelayUsage.RELAY_1))
                .create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");
        List<String> labels = detailPage.getGeneralSection().getSectionLabels();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        softly.assertThat(3).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Communication Route:").isEqualTo(labels.get(2));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpMCTDetail_GeneralSection_ValuesCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(28)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withAddress(34567)
                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2, LoadGroupEnums.RelayUsage.RELAY_1))
                .create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");
        List<String> values = detailPage.getGeneralSection().getSectionValues();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
	    softly.assertThat(3).isEqualTo(values.size());
	    softly.assertThat(values.get(2)).isEqualTo(response.get("routeName").toString());
        
	    softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpMCTDetail_AddressingSection_LabelsCorrect() {
    	SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getAddressingSection().getSectionLabels();
        
        softly.assertThat(4).isEqualTo(labels.size());
        softly.assertThat("Address Level:").isEqualTo(labels.get(0));
        softly.assertThat("Address:").isEqualTo(labels.get(1));
        softly.assertThat("Relay Usage:").isEqualTo(labels.get(3));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpMCTDetail_AddressingSection_ValuesCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> values = detailPage.getAddressingSection().getSectionValues();
	    ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
    	
    	navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
    	
	    softly.assertThat(4).isEqualTo(values.size());
	    String addressLevelRespense = response.get("level").toString();
        String addressLevel = addressLevelRespense.substring(0, 1).toUpperCase()
                + addressLevelRespense.substring(1).toLowerCase();
        softly.assertThat(values.get(0)).isEqualTo(addressLevel);
        
        softly.assertThat(values.get(1)).isEqualTo(response.get("address").toString());
        String relayUsageRespense = getResponse.path("LM_GROUP_MCT.relayUsage[0]").toString();
        String relayUsage = relayUsageRespense.substring(0, 1).toUpperCase() + relayUsageRespense.substring(1).toLowerCase();
        softly.assertThat(values.get(3)).isEqualTo(relayUsage.replace("_", " "));

        softly.assertAll();
    }
}