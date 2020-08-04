package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEditPage;

public class LoadGroupEcobeeEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpEcobeeEdit_pageTitleCorrect() {        
        Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();                 
        
        JSONObject response = pair.getValue1();
        
        String name = response.getString("name");
        Integer id = response.getInt("id");
                           
        final String EXPECTED_TITLE = "Edit Load Group: " + name;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, id);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpEcobeeEdit_requiredFieldsOnlySuccess() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
                .create();                 
        
        JSONObject response = pair.getValue1();
        
        Integer id = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, id);

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
