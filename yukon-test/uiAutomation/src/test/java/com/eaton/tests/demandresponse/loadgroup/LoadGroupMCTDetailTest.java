package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupMCTDetailTest  extends SeleniumTestSetup {
	private DriverExtensions driverExt;
	 private Integer id;
	 private String name;
	 private Integer routeId = 28;
	 private LoadGroupDetailPage detailPage;
	
	 @BeforeClass(alwaysRun=true)
	 public void beforeClass() {
		 driverExt = getDriverExt();   
	 }
	 
	 @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpMCTDetail_DeleteLoadGroup_Success() {
		 Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
															  .withCommunicationRoute(routeId) 
															  .withDisableControl(Optional.of(true))
															  .withDisableGroup(Optional.of(true)) 
															  .withKwCapacity(Optional.empty())
															  .withAddress(34567)
															  .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
															  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2,LoadGroupEnums.RelayUsage.RELAY_1 ))
															  .create(); 
															  
		 JSONObject response = pair.getValue1(); 
		 id = response.getInt("id");
		 navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
				
	     name = response.getString("name");
	     final String expected_msg = name + " deleted successfully.";
	     navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	     
	     detailPage = new LoadGroupDetailPage(driverExt, id);
	     ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal(); 
	     confirmModal.clickOkAndWaitForModalToClose();
	     
	     waitForPageToLoad("Setup", Optional.empty());
	     DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
	     String userMsg = setupPage.getUserMessage();
	     
	     assertThat(userMsg).isEqualTo(expected_msg);
	}
	
	 @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpMCTDetail_CopyLoadGroup_Success() {
		 	Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
												  .withCommunicationRoute(routeId) 
												  .withDisableControl(Optional.of(true))
												  .withDisableGroup(Optional.of(true)) 
												  .withKwCapacity(Optional.empty())
												  .withAddress(34567)
												  .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
												  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2,LoadGroupEnums.RelayUsage.RELAY_1 ))
												  .create(); 
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        final String copyName= "Copy of " + name;
	        final String expected_msg = copyName + " copied successfully.";
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
	        modal.getName().setInputValue(copyName);
	        modal.clickOkAndWaitForModalToClose();
	        
	        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
	        String userMsg = detailPage.getUserMessage();
	        
	        assertThat(userMsg).isEqualTo(expected_msg);
	    }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpMCTDetail_AssertLabels_Success() {
		 	Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
					  .withCommunicationRoute(routeId) 
					  .withDisableControl(Optional.of(true))
					  .withDisableGroup(Optional.of(true)) 
					  .withKwCapacity(Optional.empty())
					  .withAddress(34567)
					  .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
					  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2,LoadGroupEnums.RelayUsage.RELAY_1 ))
					  .create(); 
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
	        List<String> actualLabels = detailPage.getPageSection("General").getSectionLabels();
	        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
	    	
	        expectedLabels = new ArrayList<>(List.of("Address Level:", "Address:", "Relay Usage:"));
	        actualLabels = detailPage.getPageSection("Addressing").getSectionLabels();
	        assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
	        
	       
	      }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpMCTDetail_AssertValues_Success() {
	    	SoftAssertions softly = new SoftAssertions();
		 	Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
					  .withCommunicationRoute(routeId) 
					  .withDisableControl(Optional.of(true))
					  .withDisableGroup(Optional.of(true)) 
					  .withKwCapacity(Optional.empty())
					  .withAddress(34567)
					  .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
					  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2))
					  .create(); 
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
	        
	        List<String> sectionValues = detailPage.getPageSection("General").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(3);
	        softly.assertThat(sectionValues.get(0)).isEqualTo(name);
	        softly.assertThat(sectionValues.get(1)).isEqualTo("MCT Group");
	        softly.assertThat(sectionValues.get(2)).isEqualTo(getResponse.path("LM_GROUP_MCT.routeName").toString());
	        
	        sectionValues = detailPage.getPageSection("Addressing").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(4);
	        System.out.println(sectionValues.toString());
	        String addressLevelRespense = getResponse.path("LM_GROUP_MCT.level").toString();
	        String addressLevel = addressLevelRespense.substring(0, 1).toUpperCase() + addressLevelRespense.substring(1).toLowerCase();
	        softly.assertThat(sectionValues.get(0)).isEqualTo(addressLevel);
	        softly.assertThat(sectionValues.get(1)).isEqualTo(getResponse.path("LM_GROUP_MCT.address").toString());
	        String relayUsageRespense = getResponse.path("LM_GROUP_MCT.relayUsage[0]").toString();
	        String relayUsage = relayUsageRespense.substring(0, 1).toUpperCase() + relayUsageRespense.substring(1).toLowerCase();
	        softly.assertThat(sectionValues.get(3)).isEqualTo(relayUsage.replace("_", " "));
	        
	        softly.assertAll();
	    }

}
