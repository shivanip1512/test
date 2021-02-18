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
import com.eaton.pages.demandresponse.loadgroup.LoadGroupRfnExpresscomEditPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRfnExpresscomEditTest extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Integer id;
    private LoadGroupRfnExpresscomEditPage editPage;
    Builder builder;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupRfnExpresscomEditPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        refreshPage(editPage);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Rfn Express Ldgrp" + timeStamp;
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
        editPage.getProgramLoadAddress().setInputValue("89");
        editPage.getControlPriority().selectItemByValue("MEDIUM");
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");
        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.of(3));

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomEdit_Field_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);

        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(editId);

        softly.assertThat(editPage.getAddressUsage().isValueSelected("Serial")).isEqualTo(false);
        softly.assertThat(editPage.getLoadAddressUsage().isValueSelected("Program")).isEqualTo(true);
        softly.assertThat(editPage.getName().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.name").toString());
        softly.assertThat(editPage.getkWCapacity().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.kWCapacity").toString());
        softly.assertThat(editPage.getSpidAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.serviceProvider").toString());
        softly.assertThat(editPage.getGeoAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.geo").toString());
        softly.assertThat(editPage.getSubstationAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.substation").toString());
        softly.assertThat(editPage.getZipAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.zip").toString());
        softly.assertThat(editPage.getProgramLoadAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.program").toString());
        softly.assertThat(editPage.getSplinterLoadAddress().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.splinter").toString());
        softly.assertThat(editPage.getDisableControl().getCheckedValue()).isEqualTo("No");
        softly.assertThat(editPage.getDisableGroup().getCheckedValue()).isEqualTo("No");
        softly.assertAll();
    }
}