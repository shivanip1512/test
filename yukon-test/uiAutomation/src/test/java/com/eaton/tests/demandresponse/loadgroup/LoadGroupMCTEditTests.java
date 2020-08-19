package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMCTCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import org.javatuples.Pair;
import org.json.JSONObject;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

import com.eaton.pages.demandresponse.LoadGroupMCTCreatePage;

public class LoadGroupMCTEditTests extends SeleniumTestSetup {

    private LoadGroupMCTCreatePage createPage;
    WebDriver driver;
    private Integer id;
   // private LoadGroupMCTEditPage editPage;
    private DriverExtensions driverExt;
    private Random randomNum;
	private Integer address= 123;
	private Integer routeId=28;

    @BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
        Pair<JSONObject, JSONObject> pair = new LoadGroupMCTCreateBuilder.Builder(Optional.empty())
                .withCommunicationRoute(routeId)
                .withDisableControl(Optional.empty())
                .withDisableGroup(Optional.empty())
                .withKwCapacity(Optional.empty())
                .withAddress(address)
                .withMctDeviceId(259)
                .withlevel(Optional.of("MCT_ADDRESS"))
                .withRelayUsage(Arrays.asList(LoadGroupEnums.RelayUsageMCT.getRandomRelayUsage()))
                .create();
        JSONObject response = pair.getValue1();
		id = response.getInt("id");
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		//editPage = new LoadGroupMCTEditPage(driverExt, id);
	}

/*	@AfterMethod
	public void afterMethod() {
		refreshPage(editPage);
	}*/

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreateMCT_RequiredFieldsWithBronzeAddressSuccess() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByText("MCT Group");

        waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue("2");
        createPage.getRelayMCT().setTrueFalseByName("Relay 2", true);

        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(false);
        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);

    }

}
