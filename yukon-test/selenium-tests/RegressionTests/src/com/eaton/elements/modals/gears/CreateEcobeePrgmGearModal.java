package com.eaton.elements.modals.gears;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateEcobeePrgmGearModal extends CreateGearsModal {

    public CreateEcobeePrgmGearModal(WebDriver driver, String modalName) {
        super(driver, modalName);
    }

    // Control Parameters
    public TrueFalseCheckboxElement getMandatory() {
        return new TrueFalseCheckboxElement(this.driver, "fields.mandatory", getModal());
    }

    // TODO Control Percent element does not have a unique way to select it

    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", getModal());
    }
    
    // TODO Temperature Setpoint Offset element does not have a unique way to select it
    
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driver, "fields.mode", getModal());
    }

    // Optional Attributes
    // TODO Group Capacity Reduction element does not have a unique way to select it

    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", getModal());
    }
    
    //RampIn/RampOut
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampIn", getModal());                
    }
    
    public TrueFalseCheckboxElement getRampOut() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampOut", getModal());
    }
}
