package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Optional;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMCTCreateBuilder;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupMCTEditPage;

public class LoadGroupMCTEditTests extends SeleniumTestSetup {

    WebDriver driver;
    private Integer id;
    private LoadGroupMCTEditPage editPage;
    private DriverExtensions driverExt;
    private Integer routeId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        routeId = TestDbDataType.CommunicationRouteData.ACCU710A.getId();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpMCTEdit_AllFieldsWithBronzeAddress_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited MCT Ldgrp " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        Integer mctMeter = TestDbDataType.MeterData.MCT_310IL_ID.getId();

        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(routeId)
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withMctDeviceId(mctMeter)
                .withlevel(LoadGroupEnums.AddressLevelMCT.MCT_ADDRESS)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2))
                .create();

        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage = new LoadGroupMCTEditPage(driverExt, id);
        editPage.getName().setInputValue(name);
        String commRoute = TestDbDataType.CommunicationRouteData.ACCU721.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);
        editPage.getAddressLevel().selectItemByValue("BRONZE");
        editPage.getAddress().setInputValue("123");
        editPage.getRelayUsage().setTrueFalseByLabel("Relay 3", "RELAY_3", true);
        editPage.getkWCapacity().setInputValue("400");
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");
        editPage.getSaveBtn().click();

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpMCTEdit_AllFieldsWithMCTAddress_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited MCT Ldgrp " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(routeId)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(true))
                .withKwCapacity(Optional.empty())
                .withAddress(34567)
                .withlevel(LoadGroupEnums.AddressLevelMCT.LEAD)
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsage.RELAY_2, LoadGroupEnums.RelayUsage.RELAY_1))
                .create();

        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage = new LoadGroupMCTEditPage(driverExt, id);
        editPage.getName().setInputValue(name);
        String commRoute = TestDbDataType.CommunicationRouteData.ARTC.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);
        editPage.getAddressLevel().selectItemByValue("MCT_ADDRESS");
        SelectMCTMeterModal mctMeterModal = this.editPage.showAndWaitMCTMeter();
        mctMeterModal.selectMeter("a_MCT-430A");
        mctMeterModal.clickOkAndWaitForModalCloseDisplayNone();

        editPage.getRelayUsage().setTrueFalseByLabel("Relay 2", "RELAY_2", false);
        editPage.getkWCapacity().setInputValue("870");
        editPage.getDisableGroup().selectValue("No");
        editPage.getDisableControl().selectValue("No");
        editPage.getSaveBtn().click();

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}