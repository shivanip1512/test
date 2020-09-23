package com.eaton.tests.demandresponse.loadgroup;

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

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.PointId;
import com.eaton.builders.drsetup.loadgroup.LoadGroupPointCreateBuilder.Builder;
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

public class LoadGroupPointDetailTest  extends SeleniumTestSetup {
	private DriverExtensions driverExt;
	 private Integer id;
	 private String name;
	 Builder builder;
	 private LoadGroupDetailPage detailPage;
	
	 @BeforeClass(alwaysRun=true)
	 public void beforeClass() {
		 driverExt = getDriverExt();   
	 }
	 
	 @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpPointDetail_DeleteLoadGroup_Success() {
		 builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
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
	     public void ldGrpPointDetail_CopyLoadGroup_Success() {
		 builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
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
	  public void ldGrpPointDetail_AssertLabels_Success() {
		  SoftAssertions softly = new SoftAssertions();
		  builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
		  Pair<JSONObject, JSONObject> pair = builder.create();

		 JSONObject response = pair.getValue1();
		 id = response.getInt("id");
		 name = response.getString("name");
	     detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	     navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	    	
	     List<String> expectedLabels = new ArrayList<>(List.of("Control Device Point:", "Control Start State:"));
	     List<String> actualLabels = detailPage.getPageSection("Point Group").getSectionLabels();
	     softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
	       
	     actualLabels = detailPage.getPageSection("Optional Attributes").getSectionLabels();
	     expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));
	     softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);       
	 }
	    
	    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void ldGrpPointDetail_AssertValues_Success() {
	    	SoftAssertions softly = new SoftAssertions();
	    	builder = LoadGroupPointCreateBuilder.buildDefaultPointLoadGroup();
			Pair<JSONObject, JSONObject> pair =builder.create();

		    JSONObject response = pair.getValue1();
		    id = response.getInt("id");
		    name = response.getString("name");
	        detailPage = new LoadGroupDetailPage(driverExt, id);
	        
	        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	        
	        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
	        
	        List<String> sectionValues = detailPage.getPageSection("Point Group").getSectionValues();
	        
	        softly.assertThat(sectionValues.size()).isEqualTo(2);
	        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_POINT.deviceUsage.name").toString() + ": " + getResponse.path("LM_GROUP_POINT.pointUsage.name").toString());
	        softly.assertThat(sectionValues.get(1)).isEqualTo("  " + getResponse.path("LM_GROUP_POINT.startControlRawState.stateText").toString());
	        
	        softly.assertAll();
	    	
	    }

}
