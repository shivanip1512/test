package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder.Builder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupVersacomEditPage;
import com.github.javafaker.Faker;

public class LoadGroupVersacomEditTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupVersacomEditPage editPage;
    private Integer id;
    private String name;
    private Faker faker;
    private Builder builder;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        faker = SeleniumTestSetup.getFaker();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_RequiredFieldsOnly_Success() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupVersacomCreateBuilder.Builder(Optional.empty())
                .withRouteId(Optional.of(TestDbDataType.CommunicationRouteData.ACCU710A))
                .withUtilityAddress(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withDisableGroup(Optional.of(false))
                .withDisableControl(Optional.of(false))
                .create();
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String editName = "AT Edit Ld group " + timeStamp;
        final String EXPECTED_MSG = editName + " saved successfully.";

        JSONObject response = pair.getValue1();
        id = response.getInt("id");

        double capacity = faker.number().randomDouble(2, 1, 9999);

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);
        
        editPage.getName().setInputValue(editName);
        String commRoute = TestDbDataType.CommunicationRouteData.ACCU711.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute); 
        editPage.getUtilityAddress().setInputValue(String.valueOf(faker.number().numberBetween(1, 254)));
        editPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        editPage.getDisableGroup().selectValue("Yes");
        editPage.getDisableControl().selectValue("Yes");

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + editName, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_SerialAddressToSectionClass_Success() {
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
        
        String commRoute = TestDbDataType.CommunicationRouteData.ALCUEASTRIVER.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);

        editPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", false);
        editPage.getAddressUsage().setTrueFalseByLabel("Section", "SECTION", true);
        editPage.getAddressUsage().setTrueFalseByLabel("Class", "CLASS", true);

        editPage.getSectionAddress().setInputValue(String.valueOf(faker.number().numberBetween(1, 255)));
        editPage.getClassAddress().setTrueFalseByLabel("10", "10", true);

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomEdit_AddressUsageToSerial_Success() {
        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
        Pair<JSONObject, JSONObject> pair = builder
                .create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String EXPECTED_MSG = name + " saved successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
        editPage = new LoadGroupVersacomEditPage(driverExt, id);

        String commRoute = TestDbDataType.CommunicationRouteData.ATCU5000.getId().toString();
        editPage.getCommunicationRoute().selectItemByValue(commRoute);

        editPage.getAddressUsage().setTrueFalseByLabel("Serial", "SERIAL", true);

        editPage.getSerialAddress().setInputValue(String.valueOf(faker.number().numberBetween(1, 99999)));

        editPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

        String userMsg = detailsPage.getUserMessage();
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
