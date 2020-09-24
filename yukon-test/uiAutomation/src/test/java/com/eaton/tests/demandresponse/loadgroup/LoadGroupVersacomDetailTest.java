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

import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupVersacomCreateBuilder.Builder;
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

public class LoadGroupVersacomDetailTest extends SeleniumTestSetup {

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
//    public void ldGrpVersacomDetail_Delete_Success() {
//        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
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
//    public void ldGrpVersacomDetail_Copy_Success() {
//        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
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
//    public void ldGrpVersacomDetail_AssertLabels_Success() {
//        SoftAssertions softly = new SoftAssertions();
//        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder.create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        List<String> expectedLabels = new ArrayList<>(List.of("Usage:"));
//        List<String> actualLabels = detailPage.getPageSection("Address Usage").getSectionLabels();
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//
//        actualLabels = detailPage.getPageSection("Addressing").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Utility Address:", "Section Address:", "", "", ""));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//
//        actualLabels = detailPage.getPageSection("Relay Usage").getSectionLabels();
//        expectedLabels = new ArrayList<>(List.of("Relay Usage:"));
//        softly.assertThat(expectedLabels).containsExactlyElementsOf(actualLabels);
//
//        softly.assertAll();
//    }
//
//    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
//    public void ldGrpVersacomDetail_AssertValues_Success() {
//        SoftAssertions softly = new SoftAssertions();
//        builder = LoadGroupVersacomCreateBuilder.buildDefaultVersacomLoadGroup();
//        Pair<JSONObject, JSONObject> pair = builder.create();
//        JSONObject response = pair.getValue1();
//        id = response.getInt("id");
//        name = response.getString("name");
//        detailPage = new LoadGroupDetailPage(driverExt, id);
//
//        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);
//
//        ExtractableResponse<?> getResponse = DrSetupGetRequest.getLoadGroup(id);
//
//        List<String> sectionValues = detailPage.getPageSection("Address Usage").getSectionValues();
//
//        softly.assertThat(sectionValues.size()).isEqualTo(1);
//        String addressUsageRespenseElement1 = getResponse.path("LM_GROUP_VERSACOM.addressUsage[0]").toString();
//        String addressUsageElement1 = addressUsageRespenseElement1.substring(0, 1).toUpperCase()
//                + addressUsageRespenseElement1.substring(1).toLowerCase();
//        String addressUsageRespenseElement2 = getResponse.path("LM_GROUP_VERSACOM.addressUsage[1]").toString();
//        String addressUsageElement2 = addressUsageRespenseElement2.substring(0, 1).toUpperCase()
//                + addressUsageRespenseElement2.substring(1).toLowerCase();
//        softly.assertThat(sectionValues.get(0)).isEqualTo(addressUsageElement1 + "," + "  " + addressUsageElement2);
//
//        sectionValues = detailPage.getPageSection("Addressing").getSectionValues();
//        softly.assertThat(sectionValues.size()).isEqualTo(5);
//        softly.assertThat(sectionValues.get(0)).isEqualTo(getResponse.path("LM_GROUP_VERSACOM.utilityAddress").toString());
//        softly.assertThat(sectionValues.get(1)).isEqualTo(getResponse.path("LM_GROUP_VERSACOM.sectionAddress").toString());
//
//        sectionValues = detailPage.getPageSection("Relay Usage").getSectionValues();
//        softly.assertThat(sectionValues.size()).isEqualTo(1);
//        String relayUsageRespense = getResponse.path("LM_GROUP_VERSACOM.relayUsage[0]").toString();
//        String relayUsage = relayUsageRespense.substring(0, 1).toUpperCase() + relayUsageRespense.substring(1).toLowerCase();
//        softly.assertThat(sectionValues.get(0)).isEqualTo(relayUsage.replace("_", " "));
//
//        softly.assertAll();
//    }
}