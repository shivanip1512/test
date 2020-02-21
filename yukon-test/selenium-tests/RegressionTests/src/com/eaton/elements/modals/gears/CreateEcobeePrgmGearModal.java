package com.eaton.elements.modals.gears;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateEcobeePrgmGearModal extends CreateGearsModal {

    public CreateEcobeePrgmGearModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
    }

    // Control Parameters
    public TrueFalseCheckboxElement getMandatory() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.mandatory", getModal());
    }

    // TODO Control Percent element does not have a unique way to select it

    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
    }
    
    // TODO Temperature Setpoint Offset element does not have a unique way to select it
    
    public RadioButtonElement getMode() {
        return new RadioButtonElement(this.driverExt, "fields.mode", getModal());
    }

    // Optional Attributes
    // TODO Group Capacity Reduction element does not have a unique way to select it

    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driverExt, "fields.whenToChangeFields.whenToChange", getModal());
    }
    
    //RampIn/RampOut
    public TrueFalseCheckboxElement getRampIn() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.rampIn", getModal());                
    }
    
    public TrueFalseCheckboxElement getRampOut() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.rampOut", getModal());
    }
}
