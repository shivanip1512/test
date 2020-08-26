package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupHoneyWellEditTests extends SeleniumTestSetup{
    
    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupEditPage editPage;
    Builder builder;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        builder = LoadGroupHoneywellCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        editPage = new LoadGroupEditPage(driverExt, id);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_PageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;
        String actualPageTitle;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        actualPageTitle = editPage.getPageTitle();
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_AllFields_Successfully() {
        String nameAfterEdit = "EditLdGrpHoneywell " + timeStamp;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage.getName().setInputValue(nameAfterEdit);
        editPage.getkWCapacity().setInputValue("215");
        editPage.getDisableControl().setValue(true);
        editPage.getDisableGroup().setValue(true);

        editPage.getSaveBtn().click();
        assertThat(editPage.getUserMessage()).isEqualTo(nameAfterEdit + " saved successfully.");
    }

}
