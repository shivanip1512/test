package com.eaton.elements.modals.gears;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateHoneywellPrgmGearModal extends CreateGearsModal {

	private DriverExtensions driverExt;
	private String modalDescribedBy = null;
	private String modalTitle = null;
	CreateHoneywellPrgmGearModal modal;

	public CreateHoneywellPrgmGearModal(DriverExtensions driverExt, Optional<String> modalName,
			Optional<String> describedBy) {
		super(driverExt, modalName, describedBy);
		this.driverExt = driverExt;
		this.getGearType().selectItemByValue("HoneywellCycle");
		SeleniumTestSetup.waitForLoadingSpinner();
	}

	// Inner class is created to handle stale element exception:
	// Because once Gear modal is getting opened
	// We have to select Gear Type to instantiate common elements in gear modal for
	// them to be visible on modal but after that DOM structure changes
	// So, created an inner class to handle that new DOM structure
	// and also overridden some methods of the parent classes as they are also
	// giving
	// stale element exception

	public class CreateHoneywellPrgmGearModalInnerClass {

		private TextEditElement gearName;
		private DropDownElement gearType;
		private WebElement controlParameterSection;
		private SwitchBtnYesNoElement mandatory;
		private RadioButtonElement mode;
		private DropDownElement cyclePeriod;
		private TextEditElement setpointOffset;
		private TextEditElement controlPercent;
		private DropDownElement howToStopControl;
		private DropDownElement whenToChange;
		private TrueFalseCheckboxElement rampInOut;
		private DropDownElement groupCapacityReduction;

		// Common fields we are instantiating in constructor and fields specific to a
		// particular gear type are instantiated at the getter method level
		public CreateHoneywellPrgmGearModalInnerClass(DriverExtensions driverExt, Optional<String> modalName,
				Optional<String> describedBy) {
			modal = new CreateHoneywellPrgmGearModal(driverExt, modalName,
					describedBy);
			gearName = new TextEditElement(driverExt, "gearName", getModal());
			gearType = new DropDownElement(driverExt, "controlMethod", getModal());
			controlParameterSection = modal.getPageSection("Control Parameters").getSection();
			mandatory = new SwitchBtnYesNoElement(driverExt, "fields.mandatory", controlParameterSection);
			howToStopControl = new DropDownElement(driverExt, "fields.howToStopControl", modal.getModal());
			groupCapacityReduction = new DropDownElement(driverExt, "fields.capacityReduction", modal.getModal());
			whenToChange = new DropDownElement(driverExt, "fields.whenToChangeFields.whenToChange", modal.getModal());
		}

		// Common fields in Control Parameters section
		// Common fields we are instantiating in constructor

		// Overridden method to handle Stale element exception
		public TextEditElement getGearName() {
			return gearName;
		}

		// Overridden method to handle Stale element exception
		public DropDownElement getGearType() {
			return gearType;
		}

		public SwitchBtnYesNoElement getMandatory() {
			return mandatory;
		}

		public DropDownElement getHowToStopControl() {
			return howToStopControl;
		}

		// Optional Attributes
		public DropDownElement getGroupCapacityReduction() {
			return groupCapacityReduction;
		}

		public DropDownElement getWhenToChange() {
			return whenToChange;
		}

		// Overridden method to handle Stale element exception
		public void clickOkAndWaitForModalCloseDisplayNone() {
			getModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
			if (modalDescribedBy != null) {
				SeleniumTestSetup.waitUntilModalInvisibleByDescribedBy(modalDescribedBy);
			} else if (modalTitle != null) {
				SeleniumTestSetup.waitUntilModalClosedByTitle(modalTitle);
			}
		}

		// Fields Specific to Cycle Gear
		// fields specific to a particular gear type are instantiated at the getter
		// method level
		public TextEditElement getControlPercent() {
			if (controlPercent == null) {
				this.controlPercent = new TextEditElement(driverExt, "fields.controlPercent", modal.getModal());
			}
			return controlPercent;
		}

		public DropDownElement getCyclePeriod() {
			if (cyclePeriod == null) {
				this.cyclePeriod = new DropDownElement(driverExt, "fields.cyclePeriodInMinutes", modal.getModal());
			}
			return cyclePeriod;
		}

		// RampIn/RampOut
		public TrueFalseCheckboxElement getRampInOut() {
			if (rampInOut == null) {
				this.rampInOut = new TrueFalseCheckboxElement(driverExt, "fields.rampInOut", modal.getModal());
			}
			return rampInOut;
		}

		// Fields Specific to Setpoint Gear
		// fields specific to a particular gear type are instantiated at the getter
		// method level
		public RadioButtonElement getMode() {
			if (mode == null) {
				this.mode = new RadioButtonElement(driverExt, "fields.mode", controlParameterSection);
			}
			return mode;
		}

		public TextEditElement getSetpointOffset() {
			if (setpointOffset == null) {
				this.setpointOffset = new TextEditElement(driverExt, "fields.setpointOffset", modal.getModal());
			}
			return setpointOffset;
		}
	}
}