package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Optional;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMCTCreateBuilder;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import org.javatuples.Pair;
import org.json.JSONObject;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupMCTEditPage;


public class LoadGroupMCTEditTests extends SeleniumTestSetup {

	WebDriver driver;
	private Integer id;
	private LoadGroupMCTEditPage editPage;
	private DriverExtensions driverExt;
	private Integer routeId = 28;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		editPage = new LoadGroupMCTEditPage(driverExt, id);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpMCTEdit_UpdateAllFields_WithBronzeAddress_Successfully() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited MCT Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";
		
		  Pair<JSONObject, JSONObject> pair = new
		  LoadGroupMCTCreateBuilder.Builder(Optional.empty())
		  .withCommunicationRoute(routeId) 
		  .withDisableControl(Optional.empty())
		  .withDisableGroup(Optional.empty()) 
		  .withKwCapacity(Optional.empty())
		  .withMctDeviceId(259) 
		  .withlevel(LoadGroupEnums.AddressLevelMCT.MCT_ADDRESS)
		  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsageMCT.RELAY_2))
		  .create(); 
		  JSONObject response = pair.getValue1(); 
		  id = response.getInt("id");

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

		editPage.getName().setInputValue(name);
		editPage.getCommunicationRoute().selectItemByText("a_CCU-721");
		editPage.getAddressLevel().selectItemByText("Bronze");
		editPage.getAddress().setInputValue("123");
		editPage.getRelayMCT().setTrueFalseByName("Relay 3", true);
		editPage.getkWCapacity().setInputValue("400");
		editPage.getDisableGroup().setValue(true);
		editPage.getDisableControl().setValue(true);
		editPage.getSaveBtn().click();

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);

	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpMCTEdit_UpdateAllFields_WithMCTAddress_Successfully() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited MCT Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";
		
		  Pair<JSONObject, JSONObject> pair = new
		  LoadGroupMCTCreateBuilder.Builder(Optional.empty())
		  .withCommunicationRoute(routeId) 
		  .withDisableControl(Optional.of(true))
		  .withDisableGroup(Optional.of(true)) 
		  .withKwCapacity(Optional.empty())
		  .withAddress(34567)
		  .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
		  .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsageMCT.RELAY_2,LoadGroupEnums.RelayUsageMCT.RELAY_1 ))
		  .create(); 
		  
		  JSONObject response = pair.getValue1(); 
		  id = response.getInt("id");

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

		editPage.getName().setInputValue(name);
		editPage.getCommunicationRoute().selectItemByText("a_RTC");
		editPage.getAddressLevel().selectItemByText("MCT Address");
        SelectMCTMeterModal mctMeterModal = this.editPage.showAndWaitMCTMeter();
        mctMeterModal.selectMeter("a_MCT-430A");
        mctMeterModal.clickOkAndWaitForModalToClose();
		editPage.showAndWaitMCTMeter().clickOkAndWait();
		editPage.getRelayMCT().setTrueFalseByName("Relay 2", false);
		editPage.getkWCapacity().setInputValue("870");
		editPage.getDisableGroup().setValue(false);
		editPage.getDisableControl().setValue(false);
		editPage.getSaveBtn().click();

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();
		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}
}