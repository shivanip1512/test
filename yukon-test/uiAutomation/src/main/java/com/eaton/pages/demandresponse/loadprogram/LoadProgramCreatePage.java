package com.eaton.pages.demandresponse.loadprogram;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TimePickerElement;
import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.modals.gears.CreateEcobeePrgmGearModal;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.modals.gears.CreateItronPrgmGearModal;
import com.eaton.elements.modals.gears.CreateMeterDisconnectPrgmModal;
import com.eaton.elements.modals.gears.CreateSepPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadProgramCreatePage extends PageBase {

	private static final String DESCRIBEDBY = "gear-create-popup-LM_DIRECT_PROGRAM";
	private static final String HONEYWELL_GEAR_MODAL_DESCRIBEDBY = "gear-create-popup-LM_HONEYWELL_PROGRAM";
	private TextEditElement name;
	private DropDownElement type;
	private DropDownElement operationalState;
	private DropDownElement constraint;
	private TextEditElement triggerOffset;
	private TextEditElement restoreOffset;

	private Section gearSection;
	private SelectBoxElement gearSelectBox;
	private WebElement gearSectionGears;
	private Button gearsCreateBtn;

	private SwitchBtnYesNoElement useWindowOne;
	private TimePickerElement startTimeWindowOne;
	private TimePickerElement stopTimeWindowOne;
	private SwitchBtnYesNoElement useWindowTwo;
	private TimePickerElement startTimeWindowTwo;
	private TimePickerElement stopTimeWindowTwo;

	private CreateDirectPrgmGearModal showCreateDirectPrgmGearModal;
	private CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal;
	private CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal;
	private CreateItronPrgmGearModal showCreateItronPrgmGearModal;
	private CreateMeterDisconnectPrgmModal showCreateMeterDiconnectPrgmModal;
	private CreateSepPrgmGearModal showCreateSepPrgmGearModal;

	private LoadGroupsTab loadGroupsTab;

	private Button save;
	private Button cancel;

	// Common fields we are instantiating in constructor and fields specific to a
	// particular program type are instantiated at the getter method level
	// Like for below common fields, object is created at the constructor level.
	// name;
	// type;
	// operationalState;
	// constraint;
	// triggerOffset;
	// restoreOffset;
	// gearSection;
	// gearSectionGears;
	// gearsCreateBtn;
	// useWindowOne;
	// startTimeWindowOne;
	// stopTimeWindowOne;
	// useWindowTwo;
	// startTimeWindowTwo;
	// stopTimeWindowTwo;
	// loadGroupsTab;
	// save;
	// cancel;
	//
	// and for field
	// Like "showCreateDirectPrgmGearModal" it will be created after clicking create
	// gear button, once we select Program Type as Direct. So, created this field at
	// the getter method level and likewise for other Program Types
	
	public LoadProgramCreatePage(DriverExtensions driverExt) {
		super(driverExt);

		requiresLogin = true;
		pageUrl = Urls.DemandResponse.LOAD_PROGRAM_CREATE;
		name = new TextEditElement(this.driverExt, "name");
		type = new DropDownElement(this.driverExt, "type");
		operationalState = new DropDownElement(this.driverExt, "operationalState");
		constraint = new DropDownElement(this.driverExt, "constraint.constraintId");
		triggerOffset = new TextEditElement(this.driverExt, "triggerOffset");
		restoreOffset = new TextEditElement(this.driverExt, "restoreOffset");

		gearSection = new Section(this.driverExt, "Gears");
		gearsCreateBtn = new Button(this.driverExt, "Create");

		useWindowOne = new SwitchBtnYesNoElement(this.driverExt, "controlWindowOne");
		startTimeWindowOne = new TimePickerElement(this.driverExt, "startTimeWindowOne_inputField");
		stopTimeWindowOne = new TimePickerElement(this.driverExt, "stopTimeWindowOne_inputField");
		useWindowTwo = new SwitchBtnYesNoElement(this.driverExt, "controlWindowTwo");
		startTimeWindowTwo = new TimePickerElement(this.driverExt, "startTimeWindowTwo_inputField");
		stopTimeWindowTwo = new TimePickerElement(this.driverExt, "startTimeWindowTwo_inputField");

		loadGroupsTab = new LoadGroupsTab(this.driverExt);

		save = new Button(this.driverExt, "Save");
		cancel = new Button(this.driverExt, "Cancel");
	}

	// Common fields we are instantiating in constructor
	// General
	public TextEditElement getName() {
		return name;
	}

	public DropDownElement getType() {
		return type;
	}

	public DropDownElement getOperationalState() {
		return operationalState;
	}

	public DropDownElement getConstraint() {
		return constraint;
	}

	// Trigger Threshold Settings
	public TextEditElement getTriggerOffset() {
		return triggerOffset;
	}

	public TextEditElement getRestoreOffset() {
		return restoreOffset;
	}

	// Control Window
	public SwitchBtnYesNoElement getUseWindowOne() {
		return useWindowOne;
	}

	public TimePickerElement getStartTimeWindowOne() {
		return startTimeWindowOne;
	}

	public TimePickerElement getStopTimeWindowOne() {
		return stopTimeWindowOne;
	}

	public TimePickerElement getStartTimeWindowTwo() {
		return startTimeWindowTwo;
	}

	public TimePickerElement getStopTimeWindowTwo() {
		return stopTimeWindowTwo;
	}

	public SwitchBtnYesNoElement getUseWindowTwo() {
		return useWindowTwo;
	}

	public Button getSaveBtn() {
		return save;
	}

	public Button getCancelBtn() {
		return cancel;
	}

	public Button getGearsCreateBtn() {
		return gearsCreateBtn;
	}

	public WebElement getGears() {
		gearSelectBox = new SelectBoxElement(driverExt, gearSection.getSection());
		if (gearSectionGears == null) {
			this.gearSectionGears = gearSelectBox.getSelectBox();
		}
		
		return gearSectionGears;
	}
	
	public void clickGearByName(String gearName) {
		WebElement gearSelectBox = getGears();
		List<WebElement> gearsList = gearSelectBox.findElements(By.cssSelector(".js-gear-details-link"));

		gearsList.stream().filter(element -> element.getText().contains(gearName)).findFirst().orElseThrow().click();
	}


	public LoadGroupsTab getLoadGroupTab() {
		return loadGroupsTab;
	}

	// fields specific to a particular program type are instantiated at the method
	// level
	public CreateDirectPrgmGearModal showCreateDirectPrgmGearsModal() {
		getGearsCreateBtn().click();
		if (showCreateDirectPrgmGearModal == null) {
			this.showCreateDirectPrgmGearModal = new CreateDirectPrgmGearModal(this.driverExt, Optional.empty(),
					Optional.of(DESCRIBEDBY));
		}
		return showCreateDirectPrgmGearModal;
	}

	public CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal() {
		getGearsCreateBtn().click();
		if (showCreateEcobeePrgmGearModal == null) {
			this.showCreateEcobeePrgmGearModal = new CreateEcobeePrgmGearModal(this.driverExt, Optional.empty(),
					Optional.of(DESCRIBEDBY));
		}
		return showCreateEcobeePrgmGearModal;
	}

	public CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal() {
		getGearsCreateBtn().click();
		if (showCreateHoneywellPrgmGearModal == null) {
			this.showCreateHoneywellPrgmGearModal = new CreateHoneywellPrgmGearModal(this.driverExt, Optional.empty(),
					Optional.of(HONEYWELL_GEAR_MODAL_DESCRIBEDBY));
		}
		return showCreateHoneywellPrgmGearModal;
	}

	public CreateItronPrgmGearModal showCreateItronPrgmGearModal() {
		getGearsCreateBtn().click();
		if (showCreateItronPrgmGearModal == null) {
			this.showCreateItronPrgmGearModal = new CreateItronPrgmGearModal(this.driverExt, Optional.empty(),
					Optional.of(DESCRIBEDBY));
		}
		return showCreateItronPrgmGearModal;
	}

	public CreateMeterDisconnectPrgmModal showCreateMeterDiconnectPrgmModal() {
		getGearsCreateBtn().click();
		if (showCreateMeterDiconnectPrgmModal == null) {
			this.showCreateMeterDiconnectPrgmModal = new CreateMeterDisconnectPrgmModal(this.driverExt,
					Optional.empty(), Optional.of(DESCRIBEDBY));
		}
		return showCreateMeterDiconnectPrgmModal;
	}

	public CreateSepPrgmGearModal showCreateSepPrgmGearModal() {
		getGearsCreateBtn().click();
		if (showCreateSepPrgmGearModal == null) {
			this.showCreateSepPrgmGearModal = new CreateSepPrgmGearModal(this.driverExt, Optional.empty(),
					Optional.of(DESCRIBEDBY));
		}
		return showCreateSepPrgmGearModal;
	}
}
