package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRfnExpresscomCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupExpresscomDetailsPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRfnExpresscomDetailTests extends SeleniumTestSetup {
	private DriverExtensions driverExt;
    private LoadGroupExpresscomDetailsPage detailPage;
    private JSONObject response;
    private int id;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        
        response = pair.getValue1();
        id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupExpresscomDetailsPage(driverExt, id);
    }
    
    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscom_pageTitleCorrect() {
    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        
        response = pair.getValue1();
        int id = response.getInt("id");
        String name = response.getString("name");
        final String expected_title = "Load Group: " + name;
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        LoadGroupDetailPage editPage = new LoadGroupDetailPage(driverExt, id);

        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(expected_title);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscom_Copy_Success() {
    	setRefreshPage(true);
    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
	    int id = response.getInt("id");
	    String name = response.getString("name");
	    final String copyName= "Copy of " + name;
        final String expected_msg = copyName + " copied successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();
        
        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(expected_msg);
     }
    
    @Test(enabled = true, groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Delete_Success() {
    	setRefreshPage(true);
    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
	    int id = response.getInt("id");
	    String name = response.getString("name");
	    final String expected_msg = name + " deleted successfully.";
	    
	    navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	     
	    ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal(); 
	    confirmModal.clickOkAndWaitForModalToClose();
	     
	    waitForPageToLoad("Setup", Optional.empty());
	    DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
	    String userMsg = setupPage.getUserMessage();
	     
	    assertThat(userMsg).isEqualTo(expected_msg);
    }  
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_GeographicalAddress_LabelsCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> labels = detailPage.getGeographicalAddressSection().getSectionLabels();
    	softly.assertThat(labels.size()).isEqualTo(1);
    	softly.assertThat(labels.get(0)).isEqualTo("Address Usage:");
    	
    	softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_GeographicalAddressing_LabelsCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> labels = detailPage.getGeographicalAddressingSection().getSectionLabels();
    	softly.assertThat(labels.size()).isEqualTo(7);
    	softly.assertThat(labels.get(0)).isEqualTo("SPID:");
    	softly.assertThat(labels.get(1)).isEqualTo("GEO:");
    	softly.assertThat(labels.get(2)).isEqualTo("Substation:");
    	softly.assertThat(labels.get(3)).isEqualTo("Feeder:");
    	softly.assertThat(labels.get(4)).isEqualTo("ZIP:");
    	
    	softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_LoadAddress_LabelsCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> labels = detailPage.getLoadAddressSection().getSectionLabels();
    	softly.assertThat(labels.size()).isEqualTo(1);
    	softly.assertThat(labels.get(0)).isEqualTo("Usage:");
    	
    	softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_LoadAddressing_LabelsCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> labels = detailPage.getLoadAddressingSection().getSectionLabels();
    	softly.assertThat(labels.size()).isEqualTo(4);
    	softly.assertThat(labels.get(0)).isEqualTo("Send Loads in Control Message:");
    	softly.assertThat(labels.get(1)).isEqualTo("Loads:");
    	softly.assertThat(labels.get(2)).isEqualTo("Program:");
    	softly.assertThat(labels.get(3)).isEqualTo("Splinter:");
    	
    	softly.assertAll();
    }
   
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_GeographicalAddress_ValuesCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	List<String> actualValues = detailPage.getGeographicalAddressSection().getSectionValues();
        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
    	
    	navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
    	softly.assertThat(getResponse.path("LM_GROUP_EXPRESSCOMM.addressUsage[0]").toString()).isEqualTo(actualValues.get(0).split(",")[0]);
    	softly.assertThat(getResponse.path("LM_GROUP_EXPRESSCOMM.addressUsage[1]").toString()).isEqualTo(actualValues.get(0).split(",")[1].trim().toUpperCase());
    	softly.assertThat(getResponse.path("LM_GROUP_EXPRESSCOMM.addressUsage[2]").toString()).isEqualTo(actualValues.get(0).split(",")[2].trim().toUpperCase());    	
    	softly.assertThat(getResponse.path("LM_GROUP_EXPRESSCOMM.addressUsage[3]").toString()).isEqualTo(actualValues.get(0).split(",")[3].trim().toUpperCase());    	
    	
    	softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_GeographicalAddressing_ValuesCorrect() {
    	SoftAssertions softly = new SoftAssertions();
    	List<String> values = detailPage.getGeographicalAddressingSection().getSectionValues();
    	softly.assertThat(values.size()).isEqualTo(7);
    	softly.assertThat(response.get("serviceProvider").toString()).isEqualTo(values.get(0));
    	softly.assertThat(response.get("geo").toString()).isEqualTo(values.get(1));
    	softly.assertThat(response.get("substation").toString()).isEqualTo(values.get(2));
    	softly.assertThat(response.get("zip").toString()).isEqualTo(values.get(4));
    	
    	softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpRfnExpresscomDetail_LoadAddressing_ValuesCorrect() {
    	setRefreshPage(true);
    	SoftAssertions softly = new SoftAssertions();
    	List<String> values = detailPage.getLoadAddressingSection().getSectionValues();
        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
    	
    	navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
    	
    	softly.assertThat(values.size()).isEqualTo(4);
    	softly.assertThat("Yes").isEqualTo(values.get(0));
    	softly.assertThat(getResponse.path("LM_GROUP_EXPRESSCOMM.relayUsage[0]").toString().replace("_", " ")).isEqualTo(values.get(1).trim());
    	softly.assertThat(response.get("program").toString()).isEqualTo(values.get(2));
    	softly.assertThat(response.get("splinter").toString()).isEqualTo(values.get(3));
    	
    	softly.assertAll();
    }
}