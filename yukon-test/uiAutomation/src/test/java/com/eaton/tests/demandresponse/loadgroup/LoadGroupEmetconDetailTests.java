package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEmetconDetailPage;

public class LoadGroupEmetconDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupEmetconDetailPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
                .create();

        response = pair.getValue1();
        int id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupEmetconDetailPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_Delete_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
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
    public void ldGrEmetconDetail_Copy_Success() {
        setRefreshPage(true);
        String name = response.getString("name");
        final String copyName = "Copied " + name;
        final String expected_msg = copyName + " copied successfully.";

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(copyName);
        SeleniumTestSetup.waitForLoadingSpinner();
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_AddressingSection_Displayed() {
        assertThat(detailPage.getAddressingSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_GeneralSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getGeneralSection().getSectionLabels();

        softly.assertThat(3).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertThat("Communication Route:").isEqualTo(labels.get(2));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_GeneralSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getGeneralSection().getSectionValues();

        softly.assertThat(3).isEqualTo(values.size());
        softly.assertThat(response.get("routeName")).isEqualTo(values.get(2));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_AddressingSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getAddressingSection().getSectionLabels();

        softly.assertThat(4).isEqualTo(labels.size());
        softly.assertThat("Gold Address:").isEqualTo(labels.get(0));
        softly.assertThat("Silver Address:").isEqualTo(labels.get(1));
        softly.assertThat("Address To Use:").isEqualTo(labels.get(2));
        softly.assertThat("Relay To Use:").isEqualTo(labels.get(3));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpEmetconDetail_AddressingSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> values = detailPage.getAddressingSection().getSectionValues();

        softly.assertThat(4).isEqualTo(values.size());
        softly.assertThat(response.get("goldAddress").toString()).isEqualTo(values.get(0));
        softly.assertThat(response.get("silverAddress").toString()).isEqualTo(values.get(1));
        String addressUsageRespense = response.get("addressUsage").toString();
        String addressUsage = addressUsageRespense.substring(0, 1).toUpperCase() + addressUsageRespense.substring(1).toLowerCase();
        softly.assertThat(addressUsage + " " + "Address").isEqualTo(values.get(2));
        softly.assertThat(response.get("relayUsage")).isEqualTo("RELAY_" + values.get(3).toUpperCase());
        softly.assertAll();
    }
}