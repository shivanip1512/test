package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEmetconEditPage;
import com.github.javafaker.Faker;

public class LoadGroupEmetconEditTest extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupEmetconEditPage editPage;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = new Faker();
        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        Integer id = response.getInt("id");
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupEmetconEditPage(driverExt, id);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(editPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconEdit_Name_RequiredValidation() {
        final String EXPECTED_MSG = "Name is required.";

        editPage.getName().clearInputValue();
        editPage.getSaveBtn().click();

        String actualMsg = editPage.getName().getValidationError();
        assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconEdit_Name_UniqueValidation() {
        final String EXPECTED_MSG = "Name must be unique.";
        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        String name = response.getString("name");
        
        editPage.getName().setInputValue(name);
        editPage.getSaveBtn().click();

        String actualMsg = editPage.getName().getValidationError();
        assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconEdit_Name_InvalidCharsValidation() {
        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

        editPage.getName().setInputValue("/emetcon,|group ");
        editPage.getSaveBtn().click();

        String actualMsg = editPage.getName().getValidationError();
        assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconEdit_AllFields_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Emetcon Ldgrp" + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
                .withAddressUsage(Optional.of(LoadGroupEnums.AddressUsageEmetcon.GOLD))
                .withRelayUsage(Optional.of(LoadGroupEnums.RelayUsageEmetcon.RELAY_C))
                .withGoldAddress(Optional.of(1))
                .withSilverAddress(Optional.of(25))
                .create();
        
        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");
        
        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);

        editPage.getName().setInputValue(name);
        String commRoute = TestDbDataType.CommunicationRouteData.ACCU721.getId().toString();
        editPage.getCommuncationRoute().selectItemByValue(commRoute);
        editPage.getaddressUsage().selectByValue("Silver Address");
        editPage.getGoldAddress().setInputValue("4");
        editPage.getSilverAddress().setInputValue("23");
        editPage.getaddressrelayUsage().selectByValue("All");
        editPage.getkWCapacity().setInputValue(String.valueOf(faker.number().randomDouble(3, 0, 99999)));
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");
        editPage.getSaveBtn().click();

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}