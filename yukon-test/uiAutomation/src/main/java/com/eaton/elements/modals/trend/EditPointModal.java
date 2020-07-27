package com.eaton.elements.modals.trend;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class EditPointModal extends BaseModal{
    
    public EditPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public PickerElement getPoint() {
        return new PickerElement(driverExt, "picker-trendPointPicker");
    }
    
    public TextEditElement getLabel() {
        return new TextEditElement(driverExt, "label", getModal());
    }

    public DropDownElement getStyle() {
        return new DropDownElement(driverExt, "style", getModal());
    }
    
    public DropDownElement getType() {
        return new DropDownElement(driverExt, "type", getModal());
    }
    
    public RadioButtonElement getAxis() {
        return new RadioButtonElement(driverExt, "axis", getModal());
    }
    
    public TextEditElement getMultiplier() {
        return new TextEditElement(driverExt, "multiplier", getModal());
    }
    
/*    getColor() {
        
    }
    
    getDevice() {
        
    }*/
}
