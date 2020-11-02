package com.eaton.elements.modals.gears;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateItronPrgmGearModal extends CreateGearsModal {

    public CreateItronPrgmGearModal(DriverExtensions driverExt, Optional<String> modalName, Optional<String> describedBy) {
        super(driverExt, modalName, describedBy);
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
    
    public TextEditElement getDutyCycle() {
    	return new TextEditElement(this.driverExt, "fields.dutyCyclePercent", getModal());
    }

    public TextEditElement getCriticality() {
    	return new TextEditElement(this.driverExt, "fields.criticality", getModal());
    }
    
    public TextEditElement getGroupCapacityReduction() {
    	return new TextEditElement(this.driverExt, "fields.capacityReduction", getModal());
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
    
    public Section getPageSection(String sectionName) {
		return new Section(this.driverExt, sectionName);
	}
}
