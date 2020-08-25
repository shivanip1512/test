package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEmetconEditPage;

public class LoadGroupEmetconEditTest extends SeleniumTestSetup {
	private DriverExtensions driverExt;
	private Integer id;
	private LoadGroupEmetconEditPage editPage;
	private int routeId= 28;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
													.withCommunicationRoute(Optional.of(routeId))
													.create();
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupEmetconEditPage(driverExt, id);
	}
	
	@BeforeMethod
	public void beforeMethod() {
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + this.id + Urls.EDIT);
	}

	@AfterMethod
	public void afterMethod() {
		refreshPage(editPage);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEmetconEdit_RequiredFieldsOnly_Success() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited Emetcon Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";

		editPage.getName().setInputValue(name);

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + name, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEmetconEdit_Name_NameRequired() {
		final String EXPECTED_MSG = "Name is required.";

		editPage.getName().clearInputValue();
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEmetconEdit_Name_UniqueName() {
		Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
											.withCommunicationRoute(Optional.of(routeId))
											.create();
		JSONObject response = pair.getValue1();

		String name = response.getString("name");

		final String EXPECTED_MSG = "Name must be unique.";

		editPage.getName().setInputValue(name);
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEmetconEdit_Name_invalidChars() {
		final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

		editPage.getName().setInputValue("/emetcon,|group ");
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEmetconEdit_UpdateAllFields_Success() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited Emetcon Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";
		Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
													.withAddressUsage(Optional.of(LoadGroupEnums.AddressUsageEmetcon.GOLD))
													.withRelayUsage(Optional.of(LoadGroupEnums.RelayUsageEmetcon.RELAY_C))
													.create();
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

		editPage.getName().setInputValue(name);
		editPage.getCommuncationRoute().selectItemByText("a_CCU-721");
		editPage.getaddressUsage().setByValue("SILVER", true);
		editPage.getGoldAddress().setInputValue("2");
		editPage.getSilverAddress().setInputValue("30");
		editPage.getaddressrelayUsage().setByValue("RELAY_ALL", true);
		editPage.getkWCapacity().setInputValue("2345");
		editPage.getDisableGroup().setValue(true);
		editPage.getDisableControl().setValue(true);
		editPage.getSaveBtn().click();

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

}