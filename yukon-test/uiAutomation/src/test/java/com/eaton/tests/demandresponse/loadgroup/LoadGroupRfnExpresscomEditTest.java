package com.eaton.tests.demandresponse.loadgroup;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.*;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRfnExpresscomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRfnExpresscomCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupRfnExpresscomEditPage;

public class LoadGroupRfnExpresscomEditTest extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Integer id;
    private LoadGroupRfnExpresscomEditPage editPage;
    Builder builder;    

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        this.id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupRfnExpresscomEditPage(driverExt, id);        
    }

    @AfterMethod
    public void afterMethod() {
        refreshPage(editPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Rfn Expresscom Ldgrp " + timeStamp;
        final String expected_msg = name + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = new LoadGroupRfnExpresscomCreateBuilder.Builder(Optional.empty())
                .withSpid(Optional.of(10))
                .withProgram(Optional.of(198))
                .withProtocolPriority(Optional.of(LoadGroupEnums.ProtocolPriorityExpresscom.DEFAULT))
                .withKwCapacity(Optional.of(875.12))
                .withDisableGroup(Optional.of(false))
                .withDisableControl(Optional.of(false))
                .create();
        
        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);
        
        editPage.getName().setInputValue(name);
        editPage.getSpidAddress().setInputValue("251");
        editPage.getProgram().setInputValue("89");
        editPage.getControPriority().selectItemByText("Medium");
        editPage.getDisableGroup().setValue(true);
        editPage.getDisableControl().setValue(true);
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.of(3));
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_EditAllFieldsWithSerial_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "Edit RfnExpresscomm" + timeStamp;
        final String expected_msg = name + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);

        editPage.getName().setInputValue(name);
        
        editPage.getSpidAddress().setInputValue("251");
        editPage.getSubstationAddress().setInputValue("");
        
        editPage.getProgram().setInputValue("89");

        editPage.getControPriority().selectItemByText("Medium");
        editPage.getkWCapacity().setInputValue(String.valueOf(100.12));
        editPage.getDisableControl().setValue(true);
        editPage.getDisableGroup().setValue(true);
        
        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_EditAllFieldsWithoutSerial_Success() {

    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_FieldValues_Correct() {
        SoftAssertions softly = new SoftAssertions();;
        
        //We should be createing a load group with every field selected and validating that when we go to the edit page every field has the correct information
        softly.assertThat(editPage.getAddressUsage().isValueSelected("Serial")).isEqualTo(true);
        softly.assertThat(editPage.getLoadAddressUsage().isValueSelected("Program")).isEqualTo(false);
        softly.assertThat(editPage.getkWCapacity().getInputValue()).isEqualTo("310.12");
        softly.assertThat(editPage.getDisableControl().getCheckedValue()).isEqualTo("No");
        softly.assertThat(editPage.getDisableGroup().getCheckedValue()).isEqualTo("No");
        softly.assertAll();
    }
}