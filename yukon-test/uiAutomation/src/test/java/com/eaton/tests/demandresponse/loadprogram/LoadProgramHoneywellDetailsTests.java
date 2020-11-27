package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadprogram.LoadProgramCreateService;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.DemandResponseSetupPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;

public class LoadProgramHoneywellDetailsTests  extends SeleniumTestSetup {
    private DriverExtensions driverExt;
    private LoadProgramDetailPage detailPage;
	private Integer ldPgmId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        Map<String, Pair<JSONObject, JSONObject>> pair = LoadProgramCreateService.createHoneywellProgramAllFieldsWithCycleGear();
        
        Pair<JSONObject, JSONObject> programPair = pair.get("LoadProgram");
		JSONObject response = programPair.getValue1();
		ldPgmId = response.getInt("programId");

		navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + ldPgmId);
		detailPage = new LoadProgramDetailPage(driverExt, ldPgmId);
    }

    @AfterMethod
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellDetail_Delete_Success() {
        setRefreshPage(true);

        Map<String, Pair<JSONObject, JSONObject>> pair = LoadProgramCreateService.createHoneywellProgramAllFieldsWithCycleGear();
        
        Pair<JSONObject, JSONObject> programPair = pair.get("LoadProgram");
		JSONObject request = programPair.getValue0();
		JSONObject response = programPair.getValue1();
		Integer id = response.getInt("programId");	
		String name = request.getString("name");
        
        final String EXPECTED_MSG = name + " deleted successfully.";

        navigate(Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id);

        ConfirmModal confirmModal = detailPage.showDeleteLoadProgramModal();
        confirmModal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Setup", Optional.empty());
        DemandResponseSetupPage setupPage = new DemandResponseSetupPage(driverExt, Urls.Filters.LOAD_GROUP);
        String userMsg = setupPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE })
    public void ldPrgmHoneywellDetail_Copy_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String copyName = "Copy Honeywell " + timeStamp;
        final String EXPECTED_MSG = copyName + " copied successfully.";

        CopyLoadProgramModal modal = detailPage.showCopyLoadProgramModal();
        modal.getName().setInputValue(copyName);
        modal.clickOkAndWaitForModalToClose();

        waitForPageToLoad("Load Program: " + copyName, Optional.empty());
        String userMsg = detailPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

}
