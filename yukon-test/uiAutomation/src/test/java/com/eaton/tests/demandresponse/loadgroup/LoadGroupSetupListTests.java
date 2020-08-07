package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
	private String name;
	private Integer id;
	private String filterName;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {

		driverExt = getDriverExt();
		softly = new SoftAssertions();
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		filterName = "LoadGroup " + timeStamp;
		String[] ecobeeLdGrpName = { "123ecobee", "2@$Ecobeegrp", "ecobeeldgrplower", "ECOBEELDGRPUPPER", filterName };
		String[] itronLdGrpName = { "12itron", "it$ron@group" };
		
		
		for (int i = 0; i < ecobeeLdGrpName.length; i++) {
			Pair<JSONObject, JSONObject> ecobeeLdGrpCreate = new LoadGroupEcobeeCreateBuilder.Builder(
					Optional.of(ecobeeLdGrpName[i])).create();
		}

		for (int j = 0; j < itronLdGrpName.length; j++) {
			Pair<JSONObject, JSONObject> itronLdGrpCreate = new LoadGroupItronCreateBuilder.Builder(
					Optional.of(itronLdGrpName[j])).withRelay(Optional.empty()).create();
	        JSONObject response = itronLdGrpCreate.getValue1();
	        
	        name = response.getString("name");
	        id = response.getInt("id");
			
		}

		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_LIST);

		listPage = new LoadGroupListPage(driverExt);
		names = listPage.getTable().getDataRowsTextByCellIndex(1);
		types = listPage.getTable().getDataRowsTextByCellIndex(2);
	}

	@AfterMethod
	public void afterMethod() {
		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_LIST);
		listPage = new LoadGroupListPage(driverExt);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_PageTitleCorrect() {
		final String EXPECTED_TITLE = "Setup";

		String actualPageTitle = listPage.getPageTitle();

		assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
	}

	@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_ColumnHeadersCorrect() {
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
	public void ldGrpSetupList_SortTypesAscCorrectly() {
		Collections.sort(types, String.CASE_INSENSITIVE_ORDER);

		navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_ASC);

		List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);
		assertThat(types).isEqualTo(typesList);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_SortTypesDescCorrectly() {
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
	public void ldGrpSetupList_LdGroupNameLinkCorrect() {

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

		listPage.getTypes().selectItemByText("Itron Group");
		listPage.getTypes().selectItemByText("ecobee Group");

		List<String> actualTypes = listPage.getTable().getDataRowsTextByCellIndex(2);

		assertThat(actualTypes).containsOnlyElementsOf(expectedTypes);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpSetupList_FilterByName_CorrectResultsFound() {

		listPage.getName().setInputValue(filterName);
		listPage.getSaveBtn().click();

		List<String> actual = listPage.getTable().getDataRowsTextByCellIndex(1);

		assertThat(actual.get(0)).isEqualTo(filterName);
	}
}
