package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEnums;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRippleCreateBuilder.Builder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.rest.api.drsetup.DrSetupGetRequest;

import io.restassured.response.ExtractableResponse;

public class LoadGroupRippleDetailTest extends SeleniumTestSetup {

//    private DriverExtensions driverExt;
//    private Integer id;
//    private String name;
//    Builder builder;
//    private LoadGroupDetailPage detailPage;
//
//    @BeforeClass(alwaysRun = true)
//    public void beforeClass() {
//        driverExt = getDriverExt();
//    }
//
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpRippleDetail_Delete_Success() {
//        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder.create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        final String expected_msg = name + " deleted successfully.";
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
//        confirmModal.clickOkAndWaitForModalToClose();
//
//        waitForPageToLoad("Setup", Optional.empty());
//        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
//        String userMsg = setupPage.getUserMessage();
//
//        assertThat(userMsg).isEqualTo(expected_msg);
//    }
//
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpRippleDetail_Copy_Success() {
//        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder.create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        final String copyName = "Copy of " + name;
//        final String expected_msg = copyName + " copied successfully.";
//
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();
//        modal.getName().setInputValue(copyName);
//        modal.clickOkAndWaitForModalToClose();
//
//        waitForPageToLoad("Load Group: " + copyName, Optional.of(8));
//        String userMsg = detailPage.getUserMessage();
//
//        assertThat(userMsg).isEqualTo(expected_msg);
//    }
//
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpRippleDetail_AssertLabels_Success() {
//        SoftAssertions softly = new SoftAssertions();
//        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder.create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        List<String> expectedLabels = new ArrayList<>(List.of("Shed Time:", "Group:", "Area Code:"));
//        List<String> actualLabels = detailPage.getPageSection("Addressing").getSectionLabels();
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//
//        actualLabels = detailPage.getPageSection("Double Orders").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Control:", "Restore:"));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//    }
//
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpRippleDetail_AssertValues_Success() {
//        SoftAssertions softly = new SoftAssertions();
//        builder = LoadGroupRippleCreateBuilder.buildDefaultRippleLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder
//                .withShedTime(Optional.of(LoadGroupEnums.RippleShedTime.FIFTEEN_MINUTES))
//                .withGroup(Optional.of(LoadGroupEnums.RippleGroup.SIX_00))
//                .withAreaCode(Optional.of(LoadGroupEnums.RippleAreaCode.BELTRAMI))
//                .create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
//
//        List<String> sectionValues = detailPage.getPageSection("Addressing").getSectionValues();
//
//        softly.assertThat(sectionValues.size()).isEqualTo(3);
//        Integer shedTimeRespense = getResponse.path("LM_GROUP_RIPPLE.shedTime");
//        Integer shedTime = shedTimeRespense / 60;
//        softly.assertThat(sectionValues.get(0)).isEqualTo(shedTime.toString() + " " + "minutes");
//        softly.assertThat(sectionValues.get(1)).isEqualTo("LG  6.00");
//        String areaCodeRespense = getResponse.path("LM_GROUP_RIPPLE.areaCode").toString();
//        String areaCode = areaCodeRespense.substring(0, 1).toUpperCase() + areaCodeRespense.substring(1).toLowerCase();
//        softly.assertThat(sectionValues.get(2)).isEqualTo(areaCode);
//
//        softly.assertAll();
//    }
}