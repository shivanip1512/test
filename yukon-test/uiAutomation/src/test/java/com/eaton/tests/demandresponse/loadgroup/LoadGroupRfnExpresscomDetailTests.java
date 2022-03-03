package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRfnExpresscomCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupExpresscomDetailsPage;

public class LoadGroupRfnExpresscomDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupExpresscomDetailsPage detailPage;
    private JSONObject response;
    private int id;
    private String name;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();

        response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupExpresscomDetailsPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Page_TitleCorrect() {
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        
        final String expected_title = "Load Group: " + name;

        String actualPageTitle = detailPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(expected_title);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Copy_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();
        JSONObject ldGrpResp = pair.getValue1();
        int ldGrpId = ldGrpResp.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + ldGrpId);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        final String copyName = "Copied RFN Exp" + timeStamp;
        final String expected_msg = copyName + " copied successfully.";

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(enabled = true, groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Delete_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup()
                .create();
        JSONObject ldGrpResp = pair.getValue1();
        int ldGrpId = ldGrpResp.getInt("id");
        String ldGrpName = ldGrpResp.getString("name");
        final String expected_msg = ldGrpName + " deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + ldGrpId);

        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_GeographicalAddress_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getGeographicalAddressSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(1);
        softly.assertThat(labels.get(0)).isEqualTo("Address Usage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_GeographicalAddressing_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getGeographicalAddressingSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(7);
        softly.assertThat(labels.get(0)).isEqualTo("SPID:");
        softly.assertThat(labels.get(1)).isEqualTo("GEO:");
        softly.assertThat(labels.get(2)).isEqualTo("Substation:");
        softly.assertThat(labels.get(3)).isEqualTo("Feeder:");
        softly.assertThat(labels.get(4)).isEqualTo("ZIP:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_LoadAddress_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();        
        List<String> labels = detailPage.getLoadAddressSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(1);
        softly.assertThat(labels.get(0)).isEqualTo("Usage:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_LoadAddressing_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getLoadAddressingSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Send Loads in Control Message:");
        softly.assertThat(labels.get(1)).isEqualTo("Loads:");
        softly.assertThat(labels.get(2)).isEqualTo("Program:");
        softly.assertThat(labels.get(3)).isEqualTo("Splinter:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_OptionalAttributes_LabelsCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getOptionalAttributesSection().getSectionLabels();
        
        softly.assertThat(labels.size()).isEqualTo(4);
        softly.assertThat(labels.get(0)).isEqualTo("Control Priority:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_GeographicalAddress_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getGeographicalAddressSection().getSectionValues();
        List<String> addressUsageList = new ArrayList<String>(Arrays.asList(values.get(0).split(",")));
        
        JSONArray addressUsage = response.getJSONArray("addressUsage");
        
        softly.assertThat(addressUsage).contains((addressUsageList.get(0)).trim().toUpperCase());
        softly.assertThat(addressUsage).contains((addressUsageList.get(1)).trim().toUpperCase());
        softly.assertThat(addressUsage).contains((addressUsageList.get(2)).trim().toUpperCase());
        softly.assertThat(addressUsage).contains((addressUsageList.get(3)).trim().toUpperCase());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_GeographicalAddressing_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getGeographicalAddressingSection().getSectionValues();
        
        softly.assertThat(String.valueOf(response.getInt("serviceProvider"))).isEqualTo(values.get(0));
        softly.assertThat(String.valueOf(response.getInt("geo"))).isEqualTo(values.get(1));
        softly.assertThat(String.valueOf(response.getInt("substation"))).isEqualTo(values.get(2));
        //TODO need to add feeder testing here
        softly.assertThat(String.valueOf(response.getInt("zip"))).isEqualTo(values.get(4));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_LoadAddress_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getLoadAddressSection().getSectionValues();
        List<String> loadAddressList = new ArrayList<String>(Arrays.asList(values.get(0).split(",")));
        
        JSONArray addressUsage = response.getJSONArray("addressUsage");
        
        softly.assertThat(addressUsage).contains((loadAddressList.get(0)).trim().toUpperCase());
        softly.assertThat(addressUsage).contains((loadAddressList.get(1)).trim().toUpperCase());
        softly.assertThat(addressUsage).contains((loadAddressList.get(2)).trim().toUpperCase());
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_LoadAddressing_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getLoadAddressingSection().getSectionValues();
        List<String> relayUsageList = new ArrayList<String>(Arrays.asList(values.get(1).split(",")));
        
        JSONArray relayUsage = response.getJSONArray("relayUsage");

        softly.assertThat(values.size()).isEqualTo(4);
        softly.assertThat("Yes").isEqualTo(values.get(0));
        softly.assertThat(relayUsage).contains((relayUsageList.get(0)).replace(" ", "_"));
        softly.assertThat(response.get("program").toString()).isEqualTo(values.get(2));
        softly.assertThat(response.get("splinter").toString()).isEqualTo(values.get(3));
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscomDetail_OptionalAttributes_ValuesCorrect() {
        setRefreshPage(false);
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getOptionalAttributesSection().getSectionValues();
        
        softly.assertThat(values.size()).isEqualTo(4);
        softly.assertThat(response.getString("protocolPriority")).isEqualTo((values.get(0)).toUpperCase());
        softly.assertAll();
    }
}