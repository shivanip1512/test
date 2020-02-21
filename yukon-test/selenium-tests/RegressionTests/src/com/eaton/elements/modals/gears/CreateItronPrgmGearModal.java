package com.eaton.elements.modals.gears;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateItronPrgmGearModal extends CreateGearsModal {

    public CreateItronPrgmGearModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
    }

    // Control Parameters
    public DropDownElement getDutyCycleType() {
        return new DropDownElement(this.driverExt, "fields.cycleType", getModal());
    }

    // TODO Duty Cycle element does not have a unique way to select it

    public DropDownElement getDutyCyclePeriod() {
        return new DropDownElement(this.driverExt, "fields.dutyCyclePeriodInMinutes", getModal());
    }

    // TODO Criticality element does not have a unique way to select it

    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
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
