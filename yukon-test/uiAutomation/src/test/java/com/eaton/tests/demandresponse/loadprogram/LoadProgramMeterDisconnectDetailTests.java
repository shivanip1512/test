package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.gears.MeterDisconnectGearBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupMeterDisconnectCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;

public class LoadProgramMeterDisconnectDetailTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private LoadProgramDetailPage detailPage;
    private JSONObject response;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        List<JSONObject> gears = new ArrayList<JSONObject>();
        gears.add(MeterDisconnectGearBuilder.gearBuilder().build());

        Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject responseLdGrp = pairLdGrp.getValue1();
        int ldGrpId = responseLdGrp.getInt("id");

        List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));

        Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(
                ProgramEnums.ProgramType.METER_DISCONNECT_PROGRAM, gears, assignedGroupIds).withGears(gears)
                        .withName(Optional.empty())
                        .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                        .create();

        response = pair.getValue1();
        int id = response.getInt("programId");

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        detailPage = new LoadProgramDetailPage(driverExt, id);

    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmMeterDisconnectDetail_Delete_Success() {
        setRefreshPage(true);

        List<JSONObject> gears = new ArrayList<JSONObject>();
        gears.add(MeterDisconnectGearBuilder.gearBuilder().build());

        Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject responseLdGrp = pairLdGrp.getValue1();
        int ldGrpId = responseLdGrp.getInt("id");

        List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));

        Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(
                ProgramEnums.ProgramType.METER_DISCONNECT_PROGRAM, gears, assignedGroupIds).withGears(gears)
                        .withName(Optional.empty())
                        .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic)).create();

        JSONObject response = pair.getValue1();
        int id = response.getInt("programId");

        JSONObject response1 = pair.getValue0();
        String name = response1.getString("name");

        final String EXPECTED_MSG = name + " deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        ConfirmModal confirmModal = detailPage.showDeleteLoadProgramModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_PROGRAM);
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmMeterDisconnectDetail_Copy_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());;
        List<JSONObject> gears = new ArrayList<JSONObject>();
        gears.add(MeterDisconnectGearBuilder.gearBuilder().build());

        Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.empty())
                .create();
        
        JSONObject responseLdGrp = pairLdGrp.getValue1();
        int ldGrpId = responseLdGrp.getInt("id");

        List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));

        Pair<JSONObject, JSONObject> pair = new LoadProgramCreateBuilder.Builder(
                ProgramEnums.ProgramType.METER_DISCONNECT_PROGRAM, gears, assignedGroupIds).withGears(gears)
                        .withName(Optional.empty())
                        .withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
                        .create();

        JSONObject response = pair.getValue1();
        int id = response.getInt("programId");
        
        final String copyName = "Copy Disconnect " + timeStamp;

        final String EXPECTED_MSG = copyName + " copied successfully.";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Program: " + copyName, Optional.of(8));
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}
