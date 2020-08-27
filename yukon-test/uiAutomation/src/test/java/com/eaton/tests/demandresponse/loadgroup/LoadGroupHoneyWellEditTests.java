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
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupEditPage(driverExt, id);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        refreshPage(editPage);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_PageTitle_Correct() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;
        String actualPageTitle;

        actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpHoneywellEdit_AllFields_Successfully() {
        name = "EditLdGrpHoneywell " + timeStamp;

        editPage.getName().setInputValue(name);
        editPage.getkWCapacity().setInputValue("215");
        editPage.getDisableControl().setValue(true);
        editPage.getDisableGroup().setValue(true);

        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        assertThat(editPage.getUserMessage()).isEqualTo(name + " saved successfully.");
    }

}
