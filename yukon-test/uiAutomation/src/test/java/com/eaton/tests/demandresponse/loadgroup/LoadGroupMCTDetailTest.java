package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMCTCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

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
	 
	 @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
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
}
