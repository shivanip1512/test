package com.eaton.elements.modals.gears;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateSepPrgmGearModal extends CreateGearsModal {

    // CONTROL PARAMETERS
    // Control Percent
    // TODO Control Percent need to create numeric spinner element

    // Criticality
    // TODO Criticality need to create numeric spinner element

    public CreateSepPrgmGearModal(DriverExtensions driverExt, Optional<String> modalName, Optional<String> describedBy) {
        super(driverExt, modalName, describedBy);
    }

    // TrueCycleOrAdaptiveAlgorithm
    public TrueFalseCheckboxElement getTrueCycleOrAdaptiveAlgorithm() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.trueCycle", getModal());
    }

    // Ramp
    public RadioButtonElement getRamp() {
        return new RadioButtonElement(this.driverExt, "fields.rampIn", getModal());
    }

    // Mode
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driverExt, "fields.mode", getModal());
    }

    // Unit
    public RadioButtonElement getUnit() {
        return new RadioButtonElement(this.driverExt, "fields.celsiusOrFahrenheit", getModal());
    }

    // Heating Offset
    // TODO Heating Offset element does not have a unique way to select it

    // OPTIONAL ATTRIBUTES
    // Group Capacity Reduction
    // TODO Group Capacity Reduction element does not have a unique way to select it

    // When to Change
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driverExt, "fields.whenToChangeFields.whenToChange", getModal());
    }

    // RAMP IN
    // Ramp In
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.rampIn", getModal());
    }

    // Ramp Out
    public TrueFalseCheckboxElement getRampOut() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.rampOut", getModal());
    }

    // STOP CTONROL
    // How To Stop Control
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
    }
}
