package com.eaton.elements.modals;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.NumericPickerElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateTriggersModal extends BaseModal {

    private DriverExtensions driverExt;
    private DropDownElement type;
    private TrueFalseCheckboxElement usePeakTracking;
    private NumericPickerElement minRestoreOffset;
    
    public CreateTriggersModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;
        type = new DropDownElement(this.driverExt, "triggerType", getModal());
        usePeakTracking = new TrueFalseCheckboxElement(this.driverExt, "usePeak", getModal());
        minRestoreOffset = new NumericPickerElement(this.driverExt, "minRestoreOffset", getModal());
    }  
    
    //TODO Trigger Identification and Threshold Point Settings elements do not have a unique way to select them
    
    public DropDownElement getType() {
        return type;
    }
    
    public TrueFalseCheckboxElement getUsePeakTracking() {
        return usePeakTracking;
    }
    
    public NumericPickerElement getMinRestoreOffset() {
        return minRestoreOffset;
    }        
}
