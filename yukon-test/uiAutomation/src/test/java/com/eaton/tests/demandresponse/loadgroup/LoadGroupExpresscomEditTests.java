package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupExpresscomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupExpresscomCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupExpresscomEditPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupExpresscomEditTests extends SeleniumTestSetup {
	WebDriver driver;
	private DriverExtensions driverExt;
	private Random randomNum;
	private Integer id;
	private LoadGroupExpresscomEditPage editPage;
	Builder builder;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		randomNum = getRandomNum();
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpExpresscomEdit_RequiredFieldsOnly_Successfully() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String editName = "AT Edit Expresscom Ld group " + timeStamp;
		builder = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup();
		Pair<JSONObject, JSONObject> pair = builder.create();

		final String EXPECTED_MSG = editName + " saved successfully.";
		JSONObject response = pair.getValue1();
		id = response.getInt("id");

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupExpresscomEditPage(driverExt, id);
		editPage.getName().setInputValue(editName);
		// 62 = ARTC
		editPage.getCommunicationRoute().selectItemByValue("62");

		editPage.getUsage().setTrueFalseById("SPLINTER", true);
		editPage.getSplinter().setInputValue(String.valueOf(randomNum.nextInt(254)));

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + editName, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpExpresscomEdit_SerialAddressToUser_Successfully() {
		builder = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String editName = "AT Edit Expresscom Ld group " + timeStamp;
		Pair<JSONObject, JSONObject> pair = builder.withSerial(Optional.of(567)).create();
		JSONObject response = pair.getValue1();
		id = response.getInt("id");
		final String EXPECTED_MSG = editName + " saved successfully.";

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupExpresscomEditPage(driverExt, id);

		editPage.getName().setInputValue(editName);
		editPage.getGeographicalAddress().setTrueFalseByName("Serial", false);
		editPage.getGeographicalAddress().setTrueFalseByName("User", true);
		editPage.getUser().setInputValue(String.valueOf(randomNum.nextInt(65534)));
		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + editName, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpExpresscomEdit_AllFieldsWithoutSerialAddress_Successfully() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String editName = "AT Edit Expresscom Ld group " + timeStamp;
		List<String> relayUsage = new ArrayList<>();
		relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_4.getRelayUsageValue());
		relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_6.getRelayUsageValue());		

		Pair<JSONObject, JSONObject> pair = new LoadGroupExpresscomCreateBuilder.Builder(Optional.empty())
				.withDisableControl(Optional.of(true))
				.withDisableGroup(Optional.of(false))
				.withFeeder(Optional.of("1110001110001110"))
				.withGeo(Optional.of(180))
				.withKwCapacity(Optional.of(89.0))
				.withProgram(Optional.of(199))
				.withProtocolPriority(Optional.of(LoadGroupEnums.ProtocolPriorityExpresscom.HIGHEST))
				.withRelayUsage(Optional.of(relayUsage))
				.withRouteId(Optional.of(LoadGroupEnums.RouteId.AWCTPTERMINAL))
				.withSpid(Optional.of(1000))
				.withSplinter(Optional.of(200))
				.withSubstation(Optional.of(65534))
				.withUser(Optional.of(1))
				.withZip(Optional.of(16777214))
				.create();

		final String EXPECTED_MSG = editName + " saved successfully.";
		JSONObject response = pair.getValue1();
		id = response.getInt("id");

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupExpresscomEditPage(driverExt, id);
		editPage.getName().setInputValue(editName);
		// 62 = ARTC
		editPage.getCommunicationRoute().selectItemByValue("62");

		editPage.getUsage().setTrueFalseByName("Splinter", true);
		double randomDouble = randomNum.nextDouble();
		int randomInt = randomNum.nextInt(9999);
		double capacity = randomDouble + randomInt;

		editPage.getGeographicalAddress().setTrueFalseByName("GEO", true);
		editPage.getGeographicalAddress().setTrueFalseByName("Substation", true);
		editPage.getGeographicalAddress().setTrueFalseByName("Feeder", true);
		editPage.getGeographicalAddress().setTrueFalseByName("ZIP", true);
		editPage.getGeographicalAddress().setTrueFalseByName("User", true);

		editPage.getSpid().setInputValue(String.valueOf(randomNum.nextInt(65534)));
		editPage.getGeo().setInputValue(String.valueOf(randomNum.nextInt(65534)));
		editPage.getSubstation().setInputValue(String.valueOf(randomNum.nextInt(65534)));
		editPage.getFeeder().setTrueFalseByName("10", true);
		editPage.getZip().setInputValue(String.valueOf(randomNum.nextInt(65534)));
		editPage.getUser().setInputValue(String.valueOf(randomNum.nextInt(65534)));

		editPage.getLoads().setTrueFalseByName("Load 8", true);
		editPage.getProgram().setInputValue(String.valueOf(randomNum.nextInt(254)));
		editPage.getSplinter().setInputValue(String.valueOf(randomNum.nextInt(254)));

		editPage.getControlPriority().selectItemByValue("MEDIUM");
		editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
		editPage.getDisableGroup().selectValue("Yes");
		SeleniumTestSetup.moveToElement(editPage.getDisableControl().getSwitchBtn());
		editPage.getDisableControl().selectValue("No");

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + editName, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(EXPECTED_MSG).isEqualTo(userMsg);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpExpresscomEdit_FieldValues_Correct() {
		List<String> relayUsage = new ArrayList<>();
		relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_1.getRelayUsageValue());
		relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_6.getRelayUsageValue());

		SoftAssertions softly = new SoftAssertions();
		Pair<JSONObject, JSONObject> pair = new LoadGroupExpresscomCreateBuilder.Builder(Optional.empty())
				.withDisableControl(Optional.of(true))
				.withDisableGroup(Optional.of(false))
				.withFeeder(Optional.of("1110001110001110"))
				.withGeo(Optional.of(180)).withKwCapacity(Optional.of(89.0))
				.withProgram(Optional.of(50))
				.withProtocolPriority(Optional.of(LoadGroupEnums.ProtocolPriorityExpresscom.HIGHEST))
				.withRelayUsage(Optional.of(relayUsage)).withRouteId(Optional.of(LoadGroupEnums.RouteId.AWCTPTERMINAL))
				.withSpid(Optional.of(1000)).withSplinter(Optional.of(200)).withSubstation(Optional.of(65534))
				.withUser(Optional.of(1)).withZip(Optional.of(16777214)).create();

		JSONObject response = pair.getValue1();
		Integer editId = response.getInt("id");

		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);
		editPage = new LoadGroupExpresscomEditPage(driverExt, editId);

		ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(editId);

		softly.assertThat(editPage.getName().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.name").toString());
		softly.assertThat(editPage.getDisableControl().getCheckedValue()).isEqualTo("Yes");
		softly.assertThat(editPage.getDisableGroup().getCheckedValue()).isEqualTo("No");
		softly.assertThat(editPage.getFeederValueString())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.feeder").toString());
		softly.assertThat(editPage.getGeo().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.geo").toString());
		softly.assertThat(editPage.getkWCapacity().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.kWCapacity").toString());
		softly.assertThat(editPage.getProgram().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.program").toString());
		softly.assertThat(editPage.getControlPriority().getSelectedValue().toUpperCase())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.protocolPriority").toString());
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("GEO")).isEqualTo(true);
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Substation")).isEqualTo(true);
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Feeder")).isEqualTo(true);
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("ZIP")).isEqualTo(true);
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("User")).isEqualTo(true);
		softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Serial")).isEqualTo(false);
		softly.assertThat(editPage.getUsage().isValueSelected("Load")).isEqualTo(true);
		softly.assertThat(editPage.getUsage().isValueSelected("Program")).isEqualTo(true);
		softly.assertThat(editPage.getUsage().isValueSelected("Splinter")).isEqualTo(true);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_1")).isEqualTo(true);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_2")).isEqualTo(false);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_3")).isEqualTo(false);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_4")).isEqualTo(false);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_5")).isEqualTo(false);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_6")).isEqualTo(true);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_7")).isEqualTo(false);
		softly.assertThat(editPage.getLoads().isValueSelected("Load_8")).isEqualTo(false);
		softly.assertThat(editPage.getCommunicationRoute().getSelectedValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.routeName").toString());
		softly.assertThat(editPage.getSpid().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.serviceProvider").toString());
		softly.assertThat(editPage.getSplinter().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.splinter").toString());
		softly.assertThat(editPage.getSubstation().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.substation").toString());
		softly.assertThat(editPage.getUser().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.user").toString());
		softly.assertThat(editPage.getZip().getInputValue())
				.isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.zip").toString());
		softly.assertAll();
	}
}
