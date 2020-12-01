package com.eaton.tests.demandresponse.loadprogram;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterMethod;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateService;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;

public class LoadProgramEcobeeDetailsTests extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadProgramDetailPage detailPage;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        Pair<JSONObject, JSONObject> pair = LoadProgramCreateService.createEcobeeProgramWithCycleGear();
        
        JSONObject response = pair.getValue1();
        
        Integer id = response.getInt("programId");

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
    public void ldPrgmEcobeeDetail_Delete_Success() {
        setRefreshPage(true);

        Pair<JSONObject, JSONObject> pair = LoadProgramCreateService.createEcobeeProgramWithCycleGear();
        
        JSONObject response = pair.getValue1();
        int id = response.getInt("programId");
        
        JSONObject request = pair.getValue0();
        String name = request.getString("name");
        
        final String expected_msg = name + " deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        ConfirmModal confirmModal = detailPage.showDeleteLoadProgramModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmEcobeeDetail_Copy_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String copyName = "Copy Ecobee " + timeStamp;
        final String expected_msg = copyName + " copied successfully.";

        CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Program: " + copyName, Optional.empty());
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(expected_msg);
    }
}