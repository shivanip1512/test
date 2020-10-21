package com.eaton.elements.modals.gears;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateHoneywellPrgmGearModal extends CreateGearsModal {

	public CreateHoneywellPrgmGearModal(DriverExtensions driverExt, Optional<String> modalName,
			Optional<String> describedBy) {
		super(driverExt, modalName, describedBy);
	}

	// Control Parameter
	public SwitchBtnYesNoElement getMandatory() {
		WebElement section = getPageSection("Control Parameters").getSection();

		return new SwitchBtnYesNoElement(this.driverExt, "fields.mandatory", section);
	}

	public RadioButtonElement getMode() {
		WebElement section = getPageSection("Control Parameters").getSection();

		return new RadioButtonElement(this.driverExt, "fields.mode", section);
	}

	public DropDownElement getCyclePeriod() {
		return new DropDownElement(this.driverExt, "fields.cyclePeriodInMinutes", getModal());
	}
	// TODO Control Percent element does not have a unique way to select it

	public TextEditElement getSetpointOffset() {
		return new TextEditElement(this.driverExt, "fields.setpointOffset", getModal());
	}

	public DropDownElement getHowToStopControl() {
		return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
	}

	// Optional Attributes
	// TODO Group Capacity Reduction element does not have a unique way to select it

	public DropDownElement getWhenToChange() {
		return new DropDownElement(this.driverExt, "fields.whenToChangeFields.whenToChange", getModal());
	}

	// RampIn/RampOut
	public TrueFalseCheckboxElement getRampInOut() {
		return new TrueFalseCheckboxElement(this.driverExt, "fields.rampInOut", getModal());
	}

	public Section getPageSection(String sectionName) {
		return new Section(this.driverExt, sectionName);
	}
}
