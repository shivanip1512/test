package com.eaton.elements.modals.gears;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateHoneywellPrgmGearModal extends CreateGearsModal {

    public CreateHoneywellPrgmGearModal(WebDriver driver, String modalName) {
        super(driver, modalName);
    }

    // Control Parameters

    // TODO Control Percent element does not have a unique way to select it

    public DropDownElement getCyclePeriod() {
        return new DropDownElement(this.driver, "fields.cyclePeriodInMinutes", null);
    }
    
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driver, "fields.howToStopControl", null);
    }    

    // Optional Attributes
    // TODO Group Capacity Reduction element does not have a unique way to select it

    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driver, "fields.whenToChangeFields.whenToChange", null);
    }
    
    //RampIn/RampOut
    public TrueFalseCheckboxElement getRampInOut() {
        return new TrueFalseCheckboxElement(this.driver, "fields.rampInOut", null);                
    }    
}
