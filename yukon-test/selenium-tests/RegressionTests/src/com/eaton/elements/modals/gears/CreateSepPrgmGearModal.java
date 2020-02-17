package com.eaton.elements.modals.gears;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateSepPrgmGearModal extends CreateGearsModal {

    public CreateSepPrgmGearModal(WebDriver driver, String modalName) {
        super(driver, modalName);
    }

    // CONTROL PARAMETERS
    // Control Percent
    // TODO Control Percent need to create numeric spinner element

    // Criticality
    // TODO Criticality need to create numeric spinner element

    // TrueCycleOrAdaptiveAlgorithm
    public TrueFalseCheckboxElement getTrueCycleOrAdaptiveAlgorithm() {
        return new TrueFalseCheckboxElement(this.driver, "fields.trueCycle", getModal());
    }

    // Ramp
    public RadioButtonElement getRamp() {
        return new RadioButtonElement(this.driver, "fields.rampIn", getModal());
    }

    // Mode
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driver, "fields.mode", getModal());
    }

    // Unit
    public RadioButtonElement getUnit() {
        return new RadioButtonElement(this.driver, "fields.celsiusOrFahrenheit", getModal());
    }

    // Heating Offset
    // TODO Heating Offset element does not have a unique way to select it

    // OPTIONAL ATTRIBUTES
    // Group Capacity Reduction
    // TODO Group Capacity Reduction element does not have a unique way to select it

    // When to Change
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", getModal());
    }

    // RAMP IN
    // Ramp In
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampIn", getModal());
    }

    // Ramp Out
    public TrueFalseCheckboxElement getRampOut() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampOut", getModal());
    }

    // STOP CTONROL
    // How To Stop Control
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", getModal());
    }
}
