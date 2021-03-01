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

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupRippleDetailsPage;

public class LoadGroupRippleDetailTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupRippleDetailsPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup()
                .withShedTime(Optional.of(LoadGroupEnums.RippleShedTime.FIFTEEN_MINUTES))
                .withGroup(Optional.of(LoadGroupEnums.RippleGroup.SIX_00))
                .withAreaCode(Optional.of(LoadGroupEnums.RippleAreaCode.BELTRAMI))
                .create();

        response = pair.getValue1();
        int id = response.getInt("id");

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
        detailPage = new LoadGroupRippleDetailsPage(driverExt, id);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRippleDetail_Delete_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup()
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
    public void ldGrpRippleDetail_Copy_Success() {
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
    public void ldGrpRippleDetail_AddressingSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getAddressingSection().getSectionLabels();

        softly.assertThat(labels.size()).isEqualTo(3);
        softly.assertThat("Shed Time:").isEqualTo(labels.get(0));
        softly.assertThat("Group:").isEqualTo(labels.get(1));
        softly.assertThat("Area Code:").isEqualTo(labels.get(2));

        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRippleDetail_DoubleOrderSection_LabelsCorrect() {
        SoftAssertions softly = new SoftAssertions();
        List<String> labels = detailPage.getDoubleOrdersSection().getSectionLabels();

        softly.assertThat(labels.size()).isEqualTo(2);
        softly.assertThat("Control:").isEqualTo(labels.get(0));
        softly.assertThat("Restore:").isEqualTo(labels.get(1));

        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRippleDetail_AddressingSection_ValuesCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();

        List<String> values = detailPage.getAddressingSection().getSectionValues();;
        
        String rippleGroup = response.getString("group");
        String group = LoadGroupEnums.RippleGroupUi.valueOf(rippleGroup).getGroup();

        Integer shedTime = response.getInt("shedTime") / 60;
        softly.assertThat(values.get(0)).isEqualTo(shedTime.toString() + " " + "minutes");
        softly.assertThat(values.get(1)).isEqualTo(group);
        softly.assertThat((values.get(2)).toUpperCase()).isEqualTo(response.getString("areaCode"));
        softly.assertAll();
    }
}