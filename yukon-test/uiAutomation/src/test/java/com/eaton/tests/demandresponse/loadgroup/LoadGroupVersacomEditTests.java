package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupVersacomEditPage;

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
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_RequiredFieldsOnly_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Ld group " + timeStamp;
        final String EXPECTED_MSG = editName + " saved successfully.";
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");

        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);
        
        editPage.getName().setInputValue(editName);
        //28 - a_CCU-711
        editPage.getCommunicationRoute().selectItemByValue("28"); 

        editPage.getUtilityAddress().setInputValue(String.valueOf(randomNum.nextInt(254)));

        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_SerialAddressToSectionClass_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder
                .withSerial(Optional.of(567))
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);
        
        //40 = a_LCU-EASTRIVER
        editPage.getCommunicationRoute().selectItemByValue("40");

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
    public void ldGrpVersacomEdit_AddressUsageToSerial_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);

        //73 = a_TCU-5000
        editPage.getCommunicationRoute().selectItemByValue("73");

        editPage.getAddressUsage().setTrueFalseByName("Serial", true);

        editPage.getSerialAddress().setInputValue(String.valueOf(randomNum.nextInt(99999)));

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_NoRelayUsage_Successfully() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);

        //58 - a_REPEATER-921
        editPage.getCommunicationRoute().selectItemByValue("58");

        JSONArray s = response.getJSONArray("relayUsage");
        String route = s.getString(0).toString();
        String buttonName = route.replace("_", " ");
        buttonName = buttonName.substring(0, 1).toUpperCase() + buttonName.substring(1).toLowerCase();

        editPage.getRelayUsage().setTrueFalseByName(buttonName, false);
        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
