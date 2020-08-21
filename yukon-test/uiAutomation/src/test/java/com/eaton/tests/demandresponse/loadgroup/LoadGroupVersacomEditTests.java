package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder.Builder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.AddressUsage;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums.RelayUsage;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupVersacomEditPage;

public class LoadGroupVersacomEditTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupVersacomEditPage editPage;
    private Random randomNum;
    Builder builder;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
        editPage = new LoadGroupVersacomEditPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_RequiredFieldsOnly_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Ld group " + timeStamp;
        final String EXPECTED_MSG = editName + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = builder
                .withOtherAddressUsage(Optional.of(AddressUsage.UTILITY))
                .withRelayUsage(Optional.of(RelayUsage.RELAY_3))
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");

        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getName().setInputValue(editName);
        editPage.getCommunicationRoute().selectItemByText("a_CCU-711");

        editPage.getUtilityAddress().setInputValue(String.valueOf(randomNum.nextInt(254)));

        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().setValue(true);
        editPage.getDisableControl().setValue(false);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_WithSerialAddressToOtherAddressUsage_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.withSerialAddressUsage(Optional.empty())
                .withSerialAddress(Optional.of(567))
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getCommunicationRoute().selectItemByText("a_LCU-EASTRIVER");

        editPage.getAddressUsage().setTrueFalseByName("Serial", false);
        editPage.getAddressUsage().setTrueFalseByName("Section", true);
        editPage.getAddressUsage().setTrueFalseByName("Class", true);

        editPage.getSectionAddress().setInputValue(String.valueOf(randomNum.nextInt(255)));
        editPage.getClassAddress().setTrueFalseByName("10", true);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_WithoutSerialAddressToSerialAddressUsage_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder.withOtherAddressUsage(Optional.empty())
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getCommunicationRoute().selectItemByText("a_TCU-5000");

        editPage.getAddressUsage().setTrueFalseByName("Serial", true);

        editPage.getSerialAddress().setInputValue(String.valueOf(randomNum.nextInt(99999)));

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_WithRelayUsageToWithoutRelayUsage_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);

        editPage.getCommunicationRoute().selectItemByText("a_REPEATER-921");

        editPage.getRelayUsage().setTrueFalseByName("Relay 1", false);
        editPage.getRelayUsage().setTrueFalseByName("Relay 2", false);
        editPage.getRelayUsage().setTrueFalseByName("Relay 3", false);
        editPage.getRelayUsage().setTrueFalseByName("Relay 4", false);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
