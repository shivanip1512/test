package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupListPage;

public class LoadGroupSetupListTests extends SeleniumTestSetup {

    private LoadGroupListPage listPage;
    private List<String> names;
    private List<String> types;
    private DriverExtensions driverExt;
    private String name;
    private Integer id;
    private String filterName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        filterName = "LoadGroup " + timeStamp;
        String[] ecobeeLdGrpName = { "Ecobee" + timeStamp, "Jecobeegrp" + timeStamp, "Necobeeldgrplower" + timeStamp,
                "Zloadgroup" + timeStamp, filterName };
        String[] itronLdGrpName = { "Itron" + timeStamp, "Pitron" + timeStamp };

        for (int i = 0; i < ecobeeLdGrpName.length; i++) {
            new LoadGroupEcobeeCreateBuilder.Builder(Optional.of(ecobeeLdGrpName[i]))
                    .create();
        }

        for (int j = 0; j < itronLdGrpName.length; j++) {
            Pair<JSONObject, JSONObject> itronLdGrpCreate = new LoadGroupItronCreateBuilder.Builder(
                    Optional.of(itronLdGrpName[j]))
                            .withRelay(Optional.empty())
                            .create();

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
        if(getRefreshPage()) {
            refreshPage(listPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_PageTitle_Correct() {
        final String EXPECTED_TITLE = "Setup";

        String actualPageTitle = listPage.getPageTitle();

        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_ColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        final int EXPECTED_COUNT = 2;

        List<String> headers = this.listPage.getTable().getListTableHeaders();

        int actualCount = headers.size();

        softly.assertThat(actualCount).isEqualTo(EXPECTED_COUNT);
        softly.assertThat("Name").isEqualTo(headers.get(0));
        softly.assertThat("Type").isEqualTo(headers.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_SortNamesAsc_Correct() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_ASC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_SortNamesDesc_Correct() {
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(names);

        navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_NAME_DESC);

        List<String> namesList = listPage.getTable().getDataRowsTextByCellIndex(1);
        assertThat(names).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_SortTypesAsc_Correct() {
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);

        navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_ASC);

        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);
        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_SortTypesDesc_Correct() {
        Collections.sort(types, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(types);

        navigate(Urls.DemandResponse.LOAD_GROUP_SETUP_TYPE_DESC);

        List<String> typesList = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(types).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_FilterByNameDoesNotExist_NoResultsFound() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "No results found.";

        listPage.getName().setInputValue("dsdddadadadadadada");
        listPage.getSaveBtn().click();
        String userMsg = listPage.getTable().getTableMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_LdGroupNameLink_Correct() {
        listPage.getName().setInputValue(name);
        listPage.getSaveBtn().click();

        WebTableRow row = listPage.getTable().getDataRowByLinkName(name);

        String link = row.getCellLinkByIndex(0);

        assertThat(link).contains(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_Create_OpensCorrectModal() {
        setRefreshPage(true);
        String EXPECTED_CREATE_MODEL_TITLE = "Create Demand Response Object";

        CreateDRObjectModal createModel = listPage.showAndWaitCreateDemandResponseObject();
        String actualCreateModelTitle = createModel.getModalTitle();

        assertThat(EXPECTED_CREATE_MODEL_TITLE).isEqualTo(actualCreateModelTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_FilterByType_ResultsCorrect() {
        setRefreshPage(true);
        listPage.getTypes().selectItemByText("Itron Group");

        List<String> actualTypes = listPage.getTable().getDataRowsTextByCellIndex(2);

        assertThat(actualTypes).isNotEmpty().allMatch(e -> e.contains("Itron Group"));
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpSetupList_FilterByName_ResultsCorrect() {
        setRefreshPage(true);
        listPage.getName().setInputValue(filterName);
        listPage.getSaveBtn().click();

        List<String> actual = listPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(filterName).isEqualTo(actual.get(0));
    }
}
