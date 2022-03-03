package com.eaton.elements.modals.gears;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateHoneywellPrgmGearModal extends CreateGearsModal {

    public CreateHoneywellPrgmGearModal(DriverExtensions driverExt, Optional<String> modalName, Optional<String> describedBy) {
        super(driverExt, modalName, describedBy);
    }

    // Control Parameters

    // TODO Control Percent element does not have a unique way to select it

    public DropDownElement getCyclePeriod() {
        return new DropDownElement(this.driverExt, "fields.cyclePeriodInMinutes", getModal());
    }
    
    public DropDownElement getHowToStopControl() {
        return new DropDownElement(this.driverExt, "fields.howToStopControl", getModal());
    }    

    // Optional Attributes
    // TODO Group Capacity Reduction element does not have a unique way to select it

    public DropDownElement getWhenToChange() {
        return new DropDownElement(this.driverExt, "fields.whenToChangeFields.whenToChange", getModal());
    }
    
    //RampIn/RampOut
    public TrueFalseCheckboxElement getRampInOut() {
        return new TrueFalseCheckboxElement(this.driverExt, "fields.rampInOut", getModal());                
    }    
}
