package com.eaton.tests.demandresponse.loadgroup;					
					
import static org.assertj.core.api.Assertions.assertThat;					
					
import java.util.Collections;					
import java.util.List;					
					
import org.assertj.core.api.SoftAssertions;					
import org.testng.annotations.BeforeClass;					
import org.testng.annotations.BeforeMethod;					
import org.testng.annotations.Test;

import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.CreateDRObjectModal;
import com.eaton.framework.DriverExtensions;					
import com.eaton.framework.SeleniumTestSetup;					
import com.eaton.framework.TestConstants;					
import com.eaton.framework.Urls;					
					
import com.eaton.pages.demandresponse.LoadGroupListPage;					
import com.eaton.rest.api.drsetup.DrSetupCreateRequest;					
import com.eaton.rest.api.drsetup.JsonFileHelper;					
import io.restassured.response.ExtractableResponse;					
					
public class LoadGroupSetupListTests extends SeleniumTestSetup {					
					
					
					
		private LoadGroupListPage listPage;	
		private Integer ldGroupId;
		private String ldName;
		private SoftAssertions softly;			
		private List<String> names;			
		private List<String> types;			
		private DriverExtensions driverExt;			
					
		@BeforeClass(alwaysRun = true)			
		public void beforeClass() {			
					
			driverExt = getDriverExt();		
			softly = new SoftAssertions();		
					
			String payloadFile = System.getProperty("user.dir")		
					+ "\\src\\test\\resources\\payload\\payload.loadgroup\\ecobee.json";
			Object bodyLdGroup = JsonFileHelper.parseJSONFile(payloadFile);		
					
			ExtractableResponse<?> createResponseLdGrp = DrSetupCreateRequest.createLoadGroup(bodyLdGroup);
			ldGroupId = createResponseLdGrp.path("groupId");		
			ldName = 	createResponseLdGrp.path("name");
			navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_LIST);		
					
			listPage = new LoadGroupListPage(driverExt);		
			names = listPage.getTable().getDataRowsTextByCellIndex(1);		
			types = listPage.getTable().getDataRowsTextByCellIndex(2);	
		}			
					
	    @BeforeMethod				
	    public void beforeTest() {				
	        navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_LIST);				
	        listPage = new LoadGroupListPage(driverExt);				
	    }				
					
		@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_pageTitleCorrect() {			
			final String EXPECTED_TITLE = "Setup";		
					
			String actualPageTitle = listPage.getPageTitle();		
					
			assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);		
		}			
					
		@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpList_columnHeadersCorrect() {			
			final int EXPECTED_COUNT = 2;		
					
			List<String> headers = this.listPage.getTable().getListTableHeaders();		
					
			int actualCount = headers.size();		
					
			softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);		
			softly.assertThat(headers.get(0)).isEqualTo("Name");		
			softly.assertThat(headers.get(1)).isEqualTo("Type");		
			softly.assertAll();		
		}			
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_SortNamesAscCorrectly() {			
			Collections.sort(names, String.CASE_INSENSITIVE_ORDER);		
			navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_ASC);		
					
			List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);		
			assertThat(names).isEqualTo(namesList);		
		}			
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_SortNamesDescCorrectly() {			
			Collections.sort(names, String.CASE_INSENSITIVE_ORDER);		
			Collections.reverse(names);		
					
			navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_DESC);		
					
			List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);		
			assertThat(names).isEqualTo(namesList);		
		}			
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_SortTypeAscCorrectly() {			
			Collections.sort(types, String.CASE_INSENSITIVE_ORDER);		
					
			navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_ASC);		
					
			List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);		
			assertThat(types).isEqualTo(typesList);		
		}			
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_SortTypeDescCorrectly() {			
			Collections.sort(types, String.CASE_INSENSITIVE_ORDER);		
			Collections.reverse(types);		
					
			navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_DESC);		
					
			List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);		
			assertThat(types).isEqualTo(typesList);		
		}			
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_FilterByName_DoesNotExist_NoResultsFound() {			
			final String EXPECTED_MSG = "No results found.";		
					
			listPage.getName().setInputValue("dsdddadadadadadada");		
			listPage.getSaveBtn().click();		
			String userMsg = listPage.getUserMessage();		
					
			assertThat(userMsg).isEqualTo(EXPECTED_MSG);		
		}		
					
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })			
		public void ldGrpSetupList_NameLinkCorrect() {					
			listPage.getName().setInputValue("testt");		
			listPage.getSaveBtn().click();		
			
			WebTableRow row = listPage.getTable().getDataRowByName("testt");

	        String link = row.getCellLinkByIndex(0);
	        
	        assertThat(link).contains(Urls.DemandResponse.LOAD_GROUP_DETAIL.concat("1867"));	
		}		
				
		@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })				
	    public void ldGrpSetupList_CreateOpensPopupCorrect() {				
	        String EXPECTED_CREATE_MODEL_TITLE = "Create Demand Response Object";	
	        
	        CreateDRObjectModal createModel = listPage.showAndWaitCreateDemandResponseObject();				
	        String actualCreateModelTitle = createModel.getModalTitle();	
	        
	        assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);				
	    }			
}					
