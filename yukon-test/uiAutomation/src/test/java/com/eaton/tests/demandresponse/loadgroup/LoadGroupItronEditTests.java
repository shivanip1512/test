package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupItronCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupItronEditPage;

public class LoadGroupItronEditTests extends SeleniumTestSetup{
	private DriverExtensions driverExt;
	private Integer id;
	private String name;
	private Integer relay;
	private LoadGroupItronEditPage editPage;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		Pair<JSONObject, JSONObject> pair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
												.withKwCapacity(Optional.empty())
												.withRelay(Optional.empty())
												.create();
												
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		name = response.getString("name");
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupItronEditPage(driverExt, id);
	}
	
	@AfterMethod
	public void afterMethod() {
		refreshPage(editPage);
	}
	
	@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpItronEdit_PageTitle_Correct() {
		final String EXPECTED_TITLE = "Edit Load Group: " + name;

		String actualPageTitle = editPage.getPageTitle();

		assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
	}
	
	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpItronEdit_RequiredFieldsOnly_Success() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited Ecobee Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";

		Pair<JSONObject, JSONObject> pair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
												.withKwCapacity(Optional.empty())
												.withRelay(Optional.empty())
												.create();
		
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

		editPage.getName().setInputValue(name);

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + name, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}
	
	@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpItronEdit_WithRelayOnly_Success() {
		Pair<JSONObject, JSONObject> pair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
												.withKwCapacity(Optional.empty())
												.withRelay(Optional.empty())
												.create();
		
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		relay = response.getInt("virtualRelayId");
		name = response.getString("name");
		
		final String EXPECTED_MSG = name + " saved successfully.";
		
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

		//Relay value should be between 1 to 8 only, getting relay from API and updating it
		if(relay == 1) relay = relay + 1;
		else relay = relay - 1;
		
		editPage.getRelay().selectItemByText(relay.toString());

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + name, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}
}
