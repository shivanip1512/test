package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRfnExpresscomDetailTests extends SeleniumTestSetup {

//    private DriverExtensions driverExt;
//    private Integer id;
//	private String name;
//	private LoadGroupDetailPage detailPage;
//
//    @BeforeClass(alwaysRun=true)
//    public void beforeClass() {
//        driverExt = getDriverExt();                
//    }
//
//    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
//    public void ldGrpRfnExpresscom_pageTitleCorrect() {
//        final String EXPECTED_TITLE = "Load Group: AT Load Group";
//        
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "664");
//        
//        LoadGroupDetailPage editPage = new LoadGroupDetailPage(driverExt, 664);
//
//        String actualPageTitle = editPage.getPageTitle();
//        
//        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
//    }
//    
//    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
//    public void ldGrpRfnExpresscom_Copy_Success() {
//    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
//        JSONObject response = pair.getValue1();
//	    id = response.getInt("id");
//	    name = response.getString("name");
//	    final String copyName= "Copy of " + name;
//        final String expected_msg = copyName + " copied successfully.";
//        
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//        
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
//        modal.getName().setInputValue(copyName);
//        modal.clickOkAndWaitForModalToClose();
//        
//        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
//        String userMsg = detailPage.getUserMessage();
//        
//        assertThat(userMsg).isEqualTo(expected_msg);
//     }
//    
//    @Test(enabled = true, groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpRfnExpresscom_Delete_Success() {
//    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
//        JSONObject response = pair.getValue1();
//	    id = response.getInt("id");
//	    name = response.getString("name");
//	    final String expected_msg = name + " deleted successfully.";
//	    
//	    navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//	     
//	    detailPage = new LoadGroupDetailPage(driverExt, id);
//	    ConfirmModal  confirmModal = detailPage.showDeleteLoadGroupModal(); 
//	    confirmModal.clickOkAndWaitForModalToClose();
//	     
//	    waitForPageToLoad("Setup", Optional.empty());
//	    DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
//	    String userMsg = setupPage.getUserMessage();
//	     
//	    assertThat(userMsg).isEqualTo(expected_msg);
//    }  
//    
//    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
//    public void ldGrpRfnExpresscomDetail_AssertLabels_Success() {
//    	SoftAssertions softly = new SoftAssertions();
//    	Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//        
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//    	
//        List<String> expectedLabels = new ArrayList<>(List.of("Address Usage:"));
//        List<String> actualLabels = detailPage.getPageSection("Geographical Address").getSectionLabels();
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//        
//        actualLabels = detailPage.getPageSection("Geographical Addressing").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("SPID:", "GEO:", "Substation:", "Feeder:", "ZIP:", "", ""));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//        
//        actualLabels = detailPage.getPageSection("Load Address").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Usage:"));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//        
//        actualLabels = detailPage.getPageSection("Load Addressing").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Send Loads in Control Message:", "Loads:", "Program:", "Splinter:"));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//        
//        actualLabels = detailPage.getPageSection("Optional Attributes").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Control Priority:", "kW Capacity:", "Disable Group:", "Disable Control:"));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//        
//        softly.assertAll();
//    } 
}