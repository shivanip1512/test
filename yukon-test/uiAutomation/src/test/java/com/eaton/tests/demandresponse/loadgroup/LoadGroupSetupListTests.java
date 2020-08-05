package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupItronCreateBuilder;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.CreateDRObjectModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;

import com.eaton.pages.demandresponse.LoadGroupListPage;

public class LoadGroupSetupListTests extends SeleniumTestSetup {

	private LoadGroupListPage listPage;
	private SoftAssertions softly;
	private List<String> names;
	private List<String> types;
	private DriverExtensions driverExt;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {

		driverExt = getDriverExt();
		softly = new SoftAssertions();

		Pair<JSONObject, JSONObject> ecobeeLdGrpWithNumber = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of("123eco"))
				.create();
		Pair<JSONObject, JSONObject> ecobeeLdGrpWithSpecChars = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of("2@$Ecobeegrp"))
				.create();
		Pair<JSONObject, JSONObject> ecobeeLdGrpWithLowerCase = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of("ecobeeldgrplower"))
				.create();
		Pair<JSONObject, JSONObject> ecobeeLdGrpWithUpperCase = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of("ECOBEELDGRPUPPER"))
				.create();
		Pair<JSONObject, JSONObject> itronLdGrpWithNumber = new LoadGroupEcobeeCreateBuilder.Builder(Optional.of("12itron"))
				.create();
		Pair<JSONObject, JSONObject> itronLdGrpWithSpecChars = new LoadGroupItronCreateBuilder.Builder(Optional.of("it$ron@group"))
				.withRelay(Optional.empty()).create();

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
	public void ldGrpSetupList_columnHeadersCorrect() {
		final int EXPECTED_COUNT = 2;

		List<String> headers = this.listPage.getTable().getListTableHeaders();

		int actualCount = headers.size();

		softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
		softly.assertThat(headers.get(0)).isEqualTo("Name");
		softly.assertThat(headers.get(1)).isEqualTo("Type");
		softly.assertAll();
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_AscSortNamesCorrectly() {
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_ASC);

		List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
		assertThat(names).isEqualTo(namesList);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_DescSortNamesCorrectly() {
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		Collections.reverse(names);

		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_DESC);

		List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
		assertThat(names).isEqualTo(namesList);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_AscSortTypeCorrectly() {
		Collections.sort(types, String.CASE_INSENSITIVE_ORDER);

		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_ASC);

		List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);
		assertThat(types).isEqualTo(typesList);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_DescSortTypeCorrectly() {
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
	public void ldGrpSetupList_ldGroupNameLinkCorrect() {
		Pair<JSONObject, JSONObject> ecobeeLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();

		JSONObject response = ecobeeLdGrp.getValue1();
		String name = response.getString("name");
		Integer id = response.getInt("id");

		listPage.getName().setInputValue(name);
		listPage.getSaveBtn().click();

		WebTableRow row = listPage.getTable().getDataRowByName(name);

		String link = row.getCellLinkByIndex(0);

		assertThat(link).contains(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_CreateOpensPopupCorrect() {
		String EXPECTED_CREATE_MODEL_TITLE = "Create Demand Response Object";

		CreateDRObjectModal createModel = listPage.showAndWaitCreateDemandResponseObject();
		String actualCreateModelTitle = createModel.getModalTitle();

		assertThat(actualCreateModelTitle).isEqualTo(EXPECTED_CREATE_MODEL_TITLE);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_FilterByType_CorrectResultsFound() {
		List<String> expectedTypes = new ArrayList<>(List.of("Itron Group", "ecobee Group"));

		Pair<JSONObject, JSONObject> itronLdGrp = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
				.withRelay(Optional.empty()).create();

		Pair<JSONObject, JSONObject> ecobeeLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();

		listPage.getTypes().selectItemByText("Itron Group");
		listPage.getTypes().selectItemByText("ecobee Group");

		List<String> actualTypes = listPage.getTable().getDataRowsTextByCellIndex(2);

		assertThat(actualTypes).containsOnlyElementsOf(expectedTypes);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_FilterByName_CorrectResultsFound() {
		Pair<JSONObject, JSONObject> ecobeeLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();

		JSONObject response = ecobeeLdGrp.getValue1();
		String name = response.getString("name");

		listPage.getName().setInputValue(name);
		listPage.getSaveBtn().click();

		List<String> actual = listPage.getTable().getDataRowsTextByCellIndex(1);

		assertThat(actual.get(0)).isEqualTo(name);
	}
}
