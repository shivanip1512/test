package com.eaton.elements.modals.gears;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateEcobeePrgmGearModal extends CreateGearsModal {

    public CreateEcobeePrgmGearModal(DriverExtensions driverExt, Optional<String> modalName, Optional<String> describedBy) {
        super(driverExt, modalName, describedBy);
    }

    // Control Parameters
    public SwitchBtnYesNoElement getMandatory() {
        //return new TrueFalseCheckboxElement(this.driverExt, "fields.mandatory", getModal());
        return new SwitchBtnYesNoElement(this.driverExt, "fields.mandatory");
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
