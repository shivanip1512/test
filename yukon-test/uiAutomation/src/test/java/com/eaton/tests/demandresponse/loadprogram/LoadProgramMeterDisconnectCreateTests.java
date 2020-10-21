package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupMeterDisconnectCreateBuilder;
import com.eaton.elements.modals.gears.CreateMeterDisconnectPrgmModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;

import com.eaton.pages.demandresponse.LoadProgramCreatePage;

public class LoadProgramMeterDisconnectCreateTests extends SeleniumTestSetup {

	private LoadProgramCreatePage createPage;
	private DriverExtensions driverExt;
	private Random randomNum;
	String ldGrpName;
	String timeStamp;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		randomNum = getRandomNum();
		setRefreshPage(false);
		timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		ldGrpName = "MeterLoadGroup" + timeStamp;

		new LoadGroupMeterDisconnectCreateBuilder.Builder(Optional.of(ldGrpName)).create();
		navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
		createPage = new LoadProgramCreatePage(driverExt);
	}

	@AfterMethod(alwaysRun = true)
	public void afterTest() {
		refreshPage(createPage);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmMeterDisconnect_RequiredFields_Success() {
		timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String ldPrgmName = "Meter Disconnect LoadProgram" + timeStamp;
		String gearName = "Meter Disconnect Gear";

		final String EXPECTED_MSG = ldPrgmName + " saved successfully.";

        
		createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
		waitForLoadingSpinner();
		createPage.getName().setInputValue(ldPrgmName);
		
        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

		CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal();
		createGearModal.getGearName().setInputValue(gearName);
		createGearModal.getGearType().selectItemByValue("MeterDisconnect");
		waitForLoadingSpinner();
		createGearModal.clickOkAndWaitForSpinner();

		createPage.getSaveBtn().click();

		LoadProgramCreatePage createPage = new LoadProgramCreatePage(driverExt);
		
		String userMsg = createPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);

	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmMeterDisconnect_AllFields_Success() {
		timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String ldPrgmName = "Meter Disconnect LoadProgrampp" + timeStamp;
		String gearName = "Meter Disconnect Gear";

		final String EXPECTED_MSG = ldPrgmName + " saved successfully.";

        Integer triggerOffset = randomNum.nextInt(9999);
        Integer restoreOffset = randomNum.nextInt(9999);
        
		createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
		waitForLoadingSpinner();
		createPage.getName().setInputValue(ldPrgmName);

		createPage.getTriggerOffset().setInputValue(String.valueOf(triggerOffset));
		createPage.getRestoreOffset().setInputValue(String.valueOf(restoreOffset));

		createPage.getUseWindowOne().selectValue("Yes");
		createPage.getStartTimeWindowOne().setValue("12:34");
		createPage.getStopTimeWindowOne().setValue("20:45");

		createPage.getUseWindowTwo().selectValue("Yes");
		createPage.getStartTimeWindowTwo().setValue("09:12");
		createPage.getStopTimeWindowTwo().setValue("22:34");
		
        LoadGroupsTab groupsTab = createPage.getLoadGroupTab();

        groupsTab.clickTabAndWait("Load Groups");
        groupsTab.getLoadGroups().addSingleAvailable(ldGrpName);

		CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal();
		createGearModal.getGearName().setInputValue(gearName);
		createGearModal.getGearType().selectItemByValue("MeterDisconnect");
		waitForLoadingSpinner();
		createGearModal.clickOkAndWaitForSpinner();

		createPage.getSaveBtn().click();

		LoadProgramCreatePage createPage = new LoadProgramCreatePage(driverExt);

		String userMsg = createPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);

	}
	
	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPgmMeterDisconnect_GearType_ValuesCorrect() {
		List<String> expectedGearsList = new ArrayList<>(
		        List.of("Select", "Meter Disconnect"));
		
		createPage.getType().selectItemByValue("LM_METER_DISCONNECT_PROGRAM");
		waitForLoadingSpinner();
		
		CreateMeterDisconnectPrgmModal createGearModal = createPage.showCreateMeterDiconnectPrgmModal();
		waitForLoadingSpinner();
		
		List<String> actualGearsList = createGearModal.getGearType().getOptionValues();
		
		assertThat(actualGearsList).containsExactlyElementsOf(expectedGearsList);		
	}
}


