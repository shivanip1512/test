package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDigiSepEditPage;

public class LoadGroupDigiSepEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupDigiSepEditPage editPage;
    Builder builder;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        builder = LoadGroupDigiSepCreateBuilder.buildLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        editPage = new LoadGroupDigiSepEditPage(driverExt,id);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDigisepEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Load Group: " + name;
        String actualPageTitle;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDigisepEdit_AllFields_Success() {
        String nameBeforeEdit = "LdGrpDigiSep " + timeStamp;
        String nameAfterEdit = "EditLdGrpDigiSep " + timeStamp;
        builder.withName(nameBeforeEdit);
        Pair<JSONObject, JSONObject> pair = builder.create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage.getName().setInputValue(nameAfterEdit);
        editPage.getDeviceClass().selectItemByText("Smart Appliances");
        editPage.getUtilityEnrollmentGroup().setInputValue("215");
        editPage.getRampInTime().setInputValue("7000");
        editPage.getRampOutTime().setInputValue("7000");
        editPage.getkWCapacity().setInputValue("500");
        editPage.getSaveBtn().click();
        
        assertThat(editPage.getUserMessage()).isEqualTo(nameAfterEdit + " saved successfully.");
    }
}
