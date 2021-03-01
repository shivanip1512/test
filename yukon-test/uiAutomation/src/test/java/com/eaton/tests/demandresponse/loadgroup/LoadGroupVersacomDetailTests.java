package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupVersacomDetailsPage;

public class LoadGroupVersacomDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupVersacomDetailsPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup()
                .create();

        response = pair.getValue1();
        int id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupVersacomDetailsPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_Delete_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup()
                .create();
        JSONObject response = pair.getValue1();
        int id = response.getInt("id");
        String name = response.getString("name");
        final String expected_msg = name + " deleted successfully.";
        
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_Copy_Success() {
        setRefreshPage(true);
        String name = response.getString("name");
        final String copyName = "Copy of " + name;
        final String expected_msg = copyName + " copied successfully.";

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_AddressUsage_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getAddressUsageSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(1);
        softly.assertThat(labels.get(0)).isEqualTo("Usage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_AddressingSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getAddressingSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(5);
        softly.assertThat(labels.get(0)).isEqualTo("Utility Address:");
        softly.assertThat(labels.get(1)).isEqualTo("Section Address:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_RelayUsageSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getRelayUsageSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(1);
        softly.assertThat(labels.get(0)).isEqualTo("Relay Usage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_AddressUsage_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getAddressUsageSection().getSectionValues();
        List<String> addressUsageList = new ArrayList<String>(Arrays.asList(values.get(0).split(",")));
        
        JSONArray addressUsage = response.getJSONArray("addressUsage");
        
        softly.assertThat(addressUsage).contains((addressUsageList.get(0)).toUpperCase());
        softly.assertThat(addressUsage).contains((addressUsageList.get(1)).toUpperCase().trim());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_AddressingSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getAddressingSection().getSectionValues();
        
        softly.assertThat(values.size()).isEqualTo(5);
        softly.assertThat(response.get("utilityAddress").toString()).isEqualTo(values.get(0));
        softly.assertThat(response.get("sectionAddress").toString()).isEqualTo(values.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpVersacomDetail_RelayUsageSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getRelayUsageSection().getSectionValues();
        List<String> relayUsageList = new ArrayList<String>(Arrays.asList(values.get(0).split(",")));
        
        JSONArray relayUsage = response.getJSONArray("relayUsage");

        softly.assertThat(relayUsageList.size()).isEqualTo(1);
        softly.assertThat((relayUsage.getString(0)).replace("_", " ").toUpperCase()).isEqualTo(relayUsageList.get(0).toUpperCase().trim());
        softly.assertAll();
    }
}