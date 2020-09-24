package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.builders.drsetup.loadgroup.LoadGroupRfnExpresscomCreateBuilder;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;

public class LoadGroupRfnExpresscomDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private Integer id;
    private String name;
    private LoadGroupDetailPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Load Group: AT Load Group";

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "664");

        LoadGroupDetailPage editPage = new LoadGroupDetailPage(driverExt, 664);

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Copy_Success() {

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + "592");

        LoadGroupDetailPage detailPage = new LoadGroupDetailPage(driverExt, 592);

        CopyLoadGroupModal modal = detailPage.showCopyLoadGroupModal();

        String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(System.currentTimeMillis());
        String name = "AT Copied RFN Expresscom Ldgrp " + timeStamp;

        final String EXPECTED_MSG = name + " copied successfully.";

        modal.getName().setInputValue(name);

        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Group: " + name, Optional.of(8));

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt, 592);

        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(enabled = true, groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpRfnExpresscom_Delete_Success() {
        Pair<JSONObject, JSONObject> pair = LoadGroupRfnExpresscomCreateBuilder.buildDefaultRfnExpresscomLoadGroup().create();
        JSONObject response = pair.getValue1();
        id = response.getInt("id");
        name = response.getString("name");
        final String expected_msg = name + " deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        detailPage = new LoadGroupDetailPage(driverExt, id);
        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }
}
