package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.ColorPickerElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class TrendMarkerModal extends BaseModal {
    
    public TrendMarkerModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        if(modalTitle.isPresent()) {
            modalTitle = Optional.of(modalTitle.get());
        }
        
        if(describedBy.isPresent()) {
            describedBy = Optional.of(describedBy.get());
        }
    } 
    
    public TextEditElement getValue() {
        return new TextEditElement(this.driverExt, "multiplier", getModal());
    }
    
    public TextEditElement getLabel() {
        return new TextEditElement(this.driverExt, "label", getModal());
    }
    
    public RadioButtonElement getAxis() {
        return new RadioButtonElement(this.driverExt, "axis", getModal());
    } 
    
    public ColorPickerElement getColor() {
        return new ColorPickerElement(this.driverExt, "sp-replacer");
    }
}
