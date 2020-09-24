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

import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder.Builder;
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

public class LoadGroupHoneywellDetailTest extends SeleniumTestSetup {
	
	 private DriverExtensions driverExt;
	 private Integer id;
	 private String name;
	 Builder builder;
	 private LoadGroupDetailPage detailPage;
	
	 @BeforeClass(alwaysRun = true)
	 public void beforeClass() {
		 driverExt = getDriverExt();   
	 }
	 
	 @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpHoneywellDetail_Delete_Success() {
		 builder = LoadGroupHoneywellCreateBuilder.buildLoadGroup();
	     Pair<JSONObject, JSONObject> pair = builder.create();
	     JSONObject response = pair.getValue1();
	     id = response.getInt("id");
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
	    public void ldGrpHoneywellDetail_Copy_Success() {
			builder = LoadGroupHoneywellCreateBuilder.buildLoadGroup();
		    Pair<JSONObject, JSONObject> pair = builder.create();
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
	    public void ldGrpHoneywellDetail_AssertLabels_Success() {
	    	SoftAssertions softly = new SoftAssertions();
	    	builder = LoadGroupHoneywellCreateBuilder.buildLoadGroup();
		    Pair<JSONObject, JSONObject> pair = builder.create();
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	    	
	        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
	        List<String> actualLabels = detailPage.getPageSection("General").getSectionLabels();
	        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
	        
	        actualLabels = detailPage.getPageSection("Optional Attributes").getSectionLabels();
	        expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
	        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);       
	     }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpHoneywellDetail_AssertValues_Success() {
	    	SoftAssertions softly = new SoftAssertions();
	    	builder = LoadGroupHoneywellCreateBuilder.buildLoadGroup();
		    Pair<JSONObject, JSONObject> pair = builder.create();
	        JSONObject response = pair.getValue1();
	        id = response.getInt("id");
	        name = response.getString("name");
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
	        
	        List<String> sectionValues = detailPage.getPageSection("General").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(2);
	        softly.assertThat(sectionValues.get(0)).isEqualTo(name);
	        softly.assertThat(sectionValues.get(1)).isEqualTo("Honeywell Group");
	        
	        sectionValues = detailPage.getPageSection("Optional Attributes").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(3);
	        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_HONEYWELL.kWCapacity").toString());
	        softly.assertThat(sectionValues.get(1)).isEqualTo("No");
	        softly.assertThat(sectionValues.get(2)).isEqualTo("No");
	        
	        softly.assertAll();
	    }
}