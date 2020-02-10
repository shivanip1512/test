package com.eaton.elements.modals.gears;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateItronPrgmGearModal extends CreateGearsModal {

    public CreateItronPrgmGearModal(WebDriver driver, String modalName) {
        super(driver, modalName);
    }

    // Control Parameters
    public DropDownElement getDutyCycleType() {
        return new DropDownElement(this.driver, "fields.cycleType", null);
    }

    // TODO Duty Cycle element does not have a unique way to select it

    public DropDownElement getDutyCyclePeriod() {
        return new DropDownElement(this.driver, "fields.dutyCyclePeriodInMinutes", null);
    }

    // TODO Criticality element does not have a unique way to select it

    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", null);
    }

    // Optional Attributes
    // TODO Group Capacity Reduction element does not have a unique way to select it
    
    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", null);
    }
    
    //RampIn/RampOut
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampIn", null);
    }
    
    public TrueFalseCheckboxElement getRampOut() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampOut", null);
    }
}
