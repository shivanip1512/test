package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder.Builder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupVersacomDetailTest extends SeleniumTestSetup {
	private DriverExtensions driverExt;
	 private Integer id;
	 private String name;
	 Builder builder;
	 private LoadGroupDetailPage detailPage;
	
	 @BeforeClass(alwaysRun=true)
	 public void beforeClass() {
		 driverExt = getDriverExt();   
	 }
	 
	 @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	 public void ldGrpVersacomDetail_DeleteLodGroup_Success() {
		 builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
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
}