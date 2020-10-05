package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupExpresscomCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupExpresscomDetailsPage;

public class LoadGroupExpresscomDetailTests extends SeleniumTestSetup {
	private DriverExtensions driverExt;
    private LoadGroupExpresscomDetailsPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
                .create();
        
        response = pair.getValue1();
        int id = response.getInt("id");
        
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

	 
	 @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpExpresscomDetail_Delete_Success() {
		 setRefreshPage(true);
		 Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
	                .create();
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
	 public void ldGrpExpresscomDetail_Copy_Success() {
		 	setRefreshPage(true);
		 	Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
		 										.create();
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
}