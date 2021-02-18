package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupDigiSepCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;

public class LoadGroupDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupDetailPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = LoadGroupDigiSepCreateBuilder.buildLoadGroup()
                .create();

        response = pair.getValue1();
        int id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupDetailPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_GeneralSection_Displayed() {
        assertThat(detailPage.getGeneralSection().getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_OptionalAttributesSection_Displayed() {
        assertThat(detailPage.getOptionalAttributesSection().getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_GeneralSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getGeneralSection().getSectionLabels();

        softly.assertThat(2).isEqualTo(labels.size());
        softly.assertThat("Name:").isEqualTo(labels.get(0));
        softly.assertThat("Type:").isEqualTo(labels.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_GeneralSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        List<String> values = detailPage.getGeneralSection().getSectionValues();

        softly.assertThat(2).isEqualTo(values.size());
        softly.assertThat(response.get("name")).isEqualTo(values.get(0));
        softly.assertThat("Digi SEP Group").isEqualTo(values.get(1));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_OptionalAttributesSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getOptionalAttributesSection().getSectionLabels();

        softly.assertThat(3).isEqualTo(labels.size());
        softly.assertThat("kW Capacity:").isEqualTo(labels.get(0));
        softly.assertThat("Disable Group:").isEqualTo(labels.get(1));
        softly.assertThat("Disable Control:").isEqualTo(labels.get(2));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_OptionalAttributesSection_ValuesCorrect() {
        SoftAssertions softly = new SoftAssertions();
        
        List<String> values = detailPage.getOptionalAttributesSection().getSectionValues();

        String disableGroup = response.getBoolean("disableGroup") ? "Yes" : "No";
        String disableControl = response.getBoolean("disableControl") ? "Yes" : "No";

        softly.assertThat(3).isEqualTo(values.size());
        softly.assertThat(String.valueOf(response.getDouble("kWCapacity"))).isEqualTo(values.get(0));
        softly.assertThat(disableGroup).isEqualTo((values.get(1)));
        softly.assertThat(disableControl).isEqualTo(values.get(2));
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_Copy_NameRequiredValidation() {
        setRefreshPage(true);
        final String expected_msg = "Name is required.";

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().clearInputValue();
        modal.clickOkAndWaitForSpinner();

        assertThat(modal.getName().getValidationError()).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_Copy_UniqueNameValidation() {
        setRefreshPage(true);
        final String expected_msg = "Name must be unique.";
        Pair<JSONObject, JSONObject> pair = LoadGroupDigiSepCreateBuilder.buildLoadGroup()
                .create();
        
        JSONObject response = pair.getValue1();
        String name = response.getString("name");

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue(name);
        modal.clickOkAndWaitForSpinner();

        assertThat(modal.getName().getValidationError()).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_Copy_InvalidValueValidation() {
        setRefreshPage(true);
        final String expected_msg = "Name must not contain any of the following characters: / \\ , ' \" |.";

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
        modal.getName().setInputValue("/,.@4 digi");
        modal.clickOkAndWaitForSpinner();

        assertThat(modal.getName().getValidationError()).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpDetail_DeleteModal_ConfirmMessageCorrect() {
        setRefreshPage(true);
        String name = response.getString("name");
        
        final String expected_msg = "Are you sure you want to delete " + "\"" + name + "\"" + "?";
        
        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();

        assertThat(confirmModal.getConfirmMsg()).isEqualTo(expected_msg);
    }

}