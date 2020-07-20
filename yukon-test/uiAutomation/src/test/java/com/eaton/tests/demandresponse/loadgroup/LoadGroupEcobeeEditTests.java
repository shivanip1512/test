package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.LoadGroupEcobeeCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

import io.restassured.response.ExtractableResponse;

public class LoadGroupEcobeeEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpEcobeeEdit_pageTitleCorrect() {        
        Pair<JSONObject,ExtractableResponse<?>> pair = new LoadGroupEcobeeCreateBuilder.Builder()
                .withName(Optional.empty())
                .create();
        
        JSONObject json = pair.getValue0();
        ExtractableResponse<?> response = pair.getValue1();
        String id = response.path("groupId").toString();
        JSONObject group = json.getJSONObject("LM_GROUP_ECOBEE");
        String name = group.getString("name");
                
        final String EXPECTED_TITLE = "Edit Load Group: " + name;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Integer.parseInt(id));

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpEcobeeEdit_requiredFieldsOnlySuccess() {
        Pair<JSONObject,ExtractableResponse<?>> pair = new LoadGroupEcobeeCreateBuilder.Builder()
                .withName(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .create();
        
        ExtractableResponse<?> response = pair.getValue1();
        String id = response.path("groupId").toString();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Integer.parseInt(id));

        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Ecobee Ldgrp " + timeStamp;

        final String EXPECTED_MSG = name + " saved successfully.";

        editPage.getName().setInputValue(name);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, 596);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
