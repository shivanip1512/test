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
    private SoftAssertions softly;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        this.id = response.getInt("id");
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupRfnExpresscomEditPage(driverExt, id);
        softly = new SoftAssertions();

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
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        editPage.getName().setInputValue(name);
        editPage.getSaveBtn().click();
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_Name_RequiredValidation() {
        final String expected_msg = "Name is required.";
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        editPage.getName().clearInputValue();
        editPage.getSaveBtn().click();

        String actualMsg = editPage.getName().getValidationError();
        assertThat(actualMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_EditAllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "RFN Expresscomm" + timeStamp;
        final String expected_msg = name + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        editPage.getName().setInputValue(name);
        editPage.getAddressUsage().setTrueFalseByName("Serial", true);
        editPage.getSerialAddress().setInputValue("25");
        editPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
        editPage.getSplinterLoadAddress().setInputValue("20");
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
    public void ldGrpRfnExpresscomEdit_AssertEditedFieldsValueOnEditPage_Success() {
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .withProtocolPriority(Optional.of(LoadGroupEnums.ProtocolPriorityExpresscom.HIGHEST))
                .withKwCapacity(Optional.of(310.12))
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getAddressUsage().setTrueFalseByName("Serial", true);
        editPage.getSerialAddress().setInputValue("25");
        editPage.getLoadAddressUsage().setTrueFalseByName("Program", false);
        editPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
        editPage.getSplinterLoadAddress().setInputValue("20");

        editPage.getSaveBtn().click();

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        softly.assertThat(editPage.getAddressUsage().isValueSelected("Serial")).isEqualTo(true);
        softly.assertThat(editPage.getLoadAddressUsage().isValueSelected("Program")).isEqualTo(false);
        softly.assertThat(editPage.getkWCapacity().getInputValue()).isEqualTo("310.12");
        softly.assertThat(editPage.getDisableControl().getCheckedValue()).isEqualTo("No");
        softly.assertThat(editPage.getDisableGroup().getCheckedValue()).isEqualTo("No");

        softly.assertAll();
    }
}