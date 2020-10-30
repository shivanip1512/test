package com.eaton.tests.demandresponse.loadprogram;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal.CreateHoneywellPrgmGearModalInnerClass;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramCreatePage;

public class LoadProgramGearCommonTests extends SeleniumTestSetup {

	private LoadProgramCreatePage createPage;
	private DriverExtensions driverExt;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();

		createPage = new LoadProgramCreatePage(driverExt);
		navigate(Urls.DemandResponse.LOAD_PROGRAM_CREATE);
	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod() {
		refreshPage(createPage);
	}
	
	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_SetpointGearSetpointOffset_RequiredValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modalInnerClass.getSetpointOffset().clearInputValue();
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modalInnerClass.getSetpointOffset().getValidationError()).isEqualTo("Setpoint Offset is required.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_SetpointGearSetpointOffset_MaxRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modalInnerClass.getSetpointOffset().setInputValue("20");
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modalInnerClass.getSetpointOffset().getValidationError()).isEqualTo("Must be between -10 and 10.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_SetpointGearSetpointOffset_MinRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellSetpoint");
		waitForLoadingSpinner();

		modalInnerClass.getSetpointOffset().setInputValue("-100");
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modalInnerClass.getSetpointOffset().getValidationError()).isEqualTo("Must be between -10 and 10.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_CycleGearControlPercent_RequiredValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modalInnerClass.getControlPercent().clearInputValue();
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modalInnerClass.getControlPercent().getValidationError()).isEqualTo("Control Percent is required.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_CycleGearControlPercent_MaxRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modalInnerClass.getControlPercent().setInputValue("200");
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modalInnerClass.getControlPercent().getValidationError()).isEqualTo("Must be between 0 and 100.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_CycleGearControlPercent_MinRangeValidation() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		CreateHoneywellPrgmGearModalInnerClass modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(),
				Optional.empty());
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String gearName = "TestGear " + timeStamp;
		modalInnerClass.getGearName().setInputValue(gearName);
		modalInnerClass.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modalInnerClass.getControlPercent().setInputValue("-100");
		modalInnerClass.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modalInnerClass = modal.new CreateHoneywellPrgmGearModalInnerClass(this.driverExt, Optional.empty(), Optional.empty());
		assertThat(modalInnerClass.getControlPercent().getValidationError()).isEqualTo("Must be between 0 and 100.");
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldPrgmGear_InvalidGearNameErrorMessage_Displayed() {

		createPage.getType().selectItemByValue("LM_HONEYWELL_PROGRAM");
		waitForLoadingSpinner();

		CreateHoneywellPrgmGearModal modal = createPage.showCreateHoneywellPrgmGearModal();
		String gearName = "/sda@3#";
		modal.getGearName().setInputValue(gearName);
		modal.getGearType().selectItemByValue("HoneywellCycle");
		waitForLoadingSpinner();

		modal.clickOkAndWaitForModalCloseDisplayNone();

		createPage.getSaveBtn().click();
		createPage.clickGearByName(gearName);
		modal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(gearName), Optional.empty());
		assertThat(modal.getGearName().getValidationError())
				.isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
	}
}
