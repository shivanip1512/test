package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupExpresscomCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupExpresscomEditPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class LoadGroupExpresscomEditTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Faker faker;
    private Integer id;
    private LoadGroupExpresscomEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpExpresscomEdit_RequiredFieldsOnly_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Expresscom Ld group " + timeStamp;
        Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
                .create();

        final String EXPECTED_MSG = editName + " saved successfully.";
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupExpresscomEditPage(driverExt, id);
        editPage.getName().setInputValue(editName);
        String commRoute = TestDbDataType.CommunicationRouteData.ARTC.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);

        editPage.getUsage().setTrueFalseByLabel("Splinter", "SPLINTER", true);
        editPage.getSplinter().setInputValue(String.valueOf(faker.number().numberBetween(1, 254)));

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpExpresscomEdit_SerialAddressToUser_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Expresscom Ld group " + timeStamp;
        
        Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
                .withSerial(Optional.of(567))
                .create();

        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        final String EXPECTED_MSG = editName + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupExpresscomEditPage(driverExt, id);

        editPage.getName().setInputValue(editName);
        editPage.getGeographicalAddress().setTrueFalseByLabel("Serial", "SERIAL", false);
        editPage.getGeographicalAddress().setTrueFalseByLabel("User", "USER", true);
        editPage.getUser().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));
        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpExpresscomEdit_AllFieldsWithoutSerialAddress_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Expresscom Ld group " + timeStamp;
        Pair<JSONObject, JSONObject> pair = LoadGroupExpresscomCreateBuilder.buildDefaultExpresscomLoadGroup()
                .create();

        final String EXPECTED_MSG = editName + " saved successfully.";
        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupExpresscomEditPage(driverExt, id);
        editPage.getName().setInputValue(editName);
        String commRoute = TestDbDataType.CommunicationRouteData.ARTC.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);

        editPage.getUsage().setTrueFalseByLabel("Splinter", "SPLINTER", true);
        double capacity = faker.number().randomDouble(2, 1, 9999);

        editPage.getGeographicalAddress().setTrueFalseByLabel("GEO", "GEO", true);
        editPage.getGeographicalAddress().setTrueFalseByLabel("Substation", "SUBSTATION", true);
        editPage.getGeographicalAddress().setTrueFalseByLabel("Feeder", "FEEDER", true);
        editPage.getGeographicalAddress().setTrueFalseByLabel("ZIP", "ZIP", true);
        editPage.getGeographicalAddress().setTrueFalseByLabel("User", "USER", true);

        editPage.getSpid().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));
        editPage.getGeo().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));
        editPage.getSubstation().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));
        editPage.getFeeder().setTrueFalseByLabel("10", "10", true);
        editPage.getZip().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));
        editPage.getUser().setInputValue(String.valueOf(faker.number().numberBetween(1, 65534)));

        editPage.getUsage().setTrueFalseByLabel("Program", "PROGRAM", true);
        editPage.getUsage().setTrueFalseByLabel("Splinter", "SPLINTER", true);
        editPage.getUsage().setTrueFalseByLabel("Load", "LOAD", true);
        ConfirmModal confirmModal = new ConfirmModal(driverExt, Optional.empty(), Optional.of("addressing-popup"));
        confirmModal.clickOkAndWaitForSpinner();

        editPage.getLoads().setTrueFalseByLabel("Load 8", "Load_8", true);
        editPage.getProgram().setInputValue(String.valueOf(faker.number().numberBetween(1, 254)));
        editPage.getSplinter().setInputValue(String.valueOf(faker.number().numberBetween(1, 254)));

        editPage.getControlPriority().selectItemByValue("MEDIUM");
        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().selectValue("Yes");
        SeleniumTestSetup.moveToElement(editPage.getDisableControl().getSwitchBtn());
        editPage.getDisableControl().selectValue("Yes");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(EXPECTED_MSG).isEqualTo(userMsg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpExpresscomEdit_Field_ValuesCorrect() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        List<String> relayUsage = new ArrayList<>();
        relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_6.getRelayUsageValue());
        relayUsage.add(LoadGroupEnums.RelayUsageExpresscom.LOAD_1.getRelayUsageValue());

        SoftAssertions softly = new SoftAssertions();
        Pair<JSONObject, JSONObject> pair = new LoadGroupExpresscomCreateBuilder.Builder(Optional.empty())
                .withName("AT Create Expresscom Load Group" + timeStamp)
                .withDisableControl(Optional.of(true))
                .withDisableGroup(Optional.of(false))
                .withFeeder(Optional.of("1110001110001110"))
                .withGeo(Optional.of(180))
                .withKwCapacity(Optional.of(89.0))
                .withProgram(Optional.of(50))
                .withProtocolPriority(Optional.of(LoadGroupEnums.ProtocolPriorityExpresscom.HIGHEST))
                .withRelayUsage(Optional.of(relayUsage))
                .withRouteId(Optional.of(TestDbDataType.CommunicationRouteData.AWCTPTERMINAL))
                .withSpid(Optional.of(1000))
                .withSplinter(Optional.of(200))
                .withSubstation(Optional.of(65534))
                .withUser(Optional.of(1))
                .withZip(Optional.of(16777214))
                .create();

        JSONObject response = pair.getValue1();
        Integer editId = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + editId + Urls.EDIT);
        editPage = new LoadGroupExpresscomEditPage(driverExt, editId);

        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(editId);

        softly.assertThat(editPage.getName().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.name").toString());
        softly.assertThat(editPage.getDisableControl().getCheckedValue()).isEqualTo("Yes");
        softly.assertThat(editPage.getDisableGroup().getCheckedValue()).isEqualTo("No");
        softly.assertThat(editPage.getFeederValueString()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.feeder").toString());
        softly.assertThat(editPage.getGeo().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.geo").toString());
        softly.assertThat(editPage.getkWCapacity().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.kWCapacity").toString());
        softly.assertThat(editPage.getProgram().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.program").toString());
        String protocolPriorityRespense = getResponse.path("LM_GROUP_EXPRESSCOMM.protocolPriority").toString();
        String protocolPriority = protocolPriorityRespense.substring(0, 1).toUpperCase() + protocolPriorityRespense.substring(1).toLowerCase();
        softly.assertThat(editPage.getControlPriority().getSelectedValue()).isEqualTo(protocolPriority);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("GEO")).isEqualTo(true);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Substation")).isEqualTo(true);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Feeder")).isEqualTo(true);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("ZIP")).isEqualTo(true);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("User")).isEqualTo(true);
        softly.assertThat(editPage.getGeographicalAddress().isValueSelected("Serial")).isEqualTo(false);
        softly.assertThat(editPage.getUsage().isValueSelected("Load")).isEqualTo(true);
        softly.assertThat(editPage.getUsage().isValueSelected("Program")).isEqualTo(true);
        softly.assertThat(editPage.getUsage().isValueSelected("Splinter")).isEqualTo(true);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_1")).isEqualTo(true);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_2")).isEqualTo(false);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_3")).isEqualTo(false);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_4")).isEqualTo(false);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_5")).isEqualTo(false);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_6")).isEqualTo(true);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_7")).isEqualTo(false);
        softly.assertThat(editPage.getLoads().isValueSelected("Load_8")).isEqualTo(false);
        softly.assertThat(editPage.getCommunicationRoute().getSelectedValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.routeName").toString());
        softly.assertThat(editPage.getSpid().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.serviceProvider").toString());
        softly.assertThat(editPage.getSplinter().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.splinter").toString());
        softly.assertThat(editPage.getSubstation().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.substation").toString());
        softly.assertThat(editPage.getUser().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.user").toString());
        softly.assertThat(editPage.getZip().getInputValue()).isEqualTo(getResponse.path("LM_GROUP_EXPRESSCOMM.zip").toString());
        softly.assertAll();
    }
}
