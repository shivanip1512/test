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

import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEmetconDetailPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;
public class LoadGroupEmetconDetailTest extends SeleniumTestSetup {
	private DriverExtensions driverExt;
	 private Integer id;
	 private String name;
	 private LoadGroupEmetconDetailPage detailPage;
	
	 @BeforeClass(alwaysRun=true)
	 public void beforeClass() {
		 driverExt = getDriverExt();   
	 }
	 
	 @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpEmetconDetail_DeleteLoadGroup_Success() {
		 Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
	                										.create();
	     JSONObject response = pair.getValue1();
	     id = response.getInt("id");
	     name = response.getString("name");
	     final String expected_msg = name + " deleted successfully.";
	     navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	     
	     detailPage = new LoadGroupEmetconDetailPage(driverExt, id);
	     ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal(); 
	     confirmModal.clickOkAndWaitForModalToClose();
	     
	     waitForPageToLoad("Setup", Optional.empty());
	     DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
	     String userMsg = setupPage.getUserMessage();
	     
	     assertThat(userMsg).isEqualTo(expected_msg);
	}
	 
	 @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrEmetconDetail_CopyLoadGroup_Success() {
		 	Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
					.create();
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        final String copyName= "Copy of " + name;
	        final String expected_msg = copyName + " copied successfully.";
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        detailPage = new LoadGroupEmetconDetailPage(driverExt, id);
	        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
	        modal.getName().setInputValue(copyName);
	        modal.clickOkAndWaitForModalToClose();
	        
	        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
	        String userMsg = detailPage.getUserMessage();
	        
	        assertThat(userMsg).isEqualTo(expected_msg);
	    }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpEmetconDetail_AssertLabels_Success() {
	    	SoftAssertions softly = new SoftAssertions();
	    	Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
					.create();
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupEmetconDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	    	
	        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:", "Communication Route:"));
	        List<String> actualLabels = detailPage.getPageSection("General").getSectionLabels();
	        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
	        
	        actualLabels = detailPage.getPageSection("Addressing").getSectionLabels();
	        expectedLabels = new ArrayList<>(List.of("Gold Address:", "Silver Address:", "Address To Use:", "Relay To Use:"));
	        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);       
	        
	        softly.assertAll();
	    }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpEmetconDetail_AssertValues_Success() {
	    	SoftAssertions softly = new SoftAssertions();
	    	Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
					.create();
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupEmetconDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
	        
	        List<String> sectionValues = detailPage.getPageSection("General").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(3);
	        softly.assertThat(sectionValues.get(2)).isEqualTo(getResponse.path("LM_GROUP_EMETCON.routeName"));
	        
	        sectionValues = detailPage.getPageSection("Addressing").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(4);
	        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_EMETCON.goldAddress").toString());
	        softly.assertThat(sectionValues.get(1)).isEqualTo(getResponse.path("LM_GROUP_EMETCON.silverAddress").toString());
	        softly.assertThat(sectionValues.get(2).toUpperCase()).isEqualTo(getResponse.path("LM_GROUP_EMETCON.addressUsage").toString() + " " + "ADDRESS");
	        softly.assertThat("RELAY_" + sectionValues.get(3).toUpperCase()).isEqualTo(getResponse.path("LM_GROUP_EMETCON.relayUsage").toString());
	        
	        softly.assertAll();
	    }


}
