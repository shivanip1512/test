package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEditPage;

public class LoadGroupHoneyWellEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupEditPage editPage;
    Builder builder;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = new LoadGroupHoneywellCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .create();
        
        JSONObject response = pair.getValue1();
        
        id = response.getInt("id");
        name = response.getString("name");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupEditPage(driverExt, id);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;
        String actualPageTitle;

        actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_AllFields_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = new LoadGroupHoneywellCreateBuilder.Builder(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableControl(Optional.empty())
                .create();
        
        JSONObject response = pair.getValue1();
        
        Integer editId = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);
        
        String editName = "AT Edit Honeywell " + new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());;

        editPage.getName().setInputValue(editName);
        editPage.getkWCapacity().setInputValue("215");
        editPage.getDisableControl().selectValue("Yes");
        editPage.getDisableGroup().selectValue("Yes");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        assertThat(editPage.getUserMessage()).isEqualTo(editName + " saved successfully.");
    }

}
