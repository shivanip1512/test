package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupMeterDisconnectCreateBuilder;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;

public class LoadGroupMeterDisconnectDetailTest extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadGroupDetailPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpMeterDisconnectDetail_Delete_Success() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject response = pair.getValue1();

        Integer id = response.getInt("id"); 
        String name = response.getString("name");
        
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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpItronDetail_DeleteModal_ConfirmMessageCorrect() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject response = pair.getValue1();

        Integer id = response.getInt("id"); 
        String name = response.getString("name");
        final String expected_msg = "Are you sure you want to delete " + "\"" + name + "\"" + "?";
        navigate(Urls.DemandResponse.LOAD_GROUP_DETAIL + id);

        detailPage = new LoadGroupDetailPage(driverExt, id);
        ConfirmModal confirmModal = detailPage.showDeleteLoadGroupModal();

        assertThat(confirmModal.getConfirmMsg()).isEqualTo(expected_msg);
    }
}
