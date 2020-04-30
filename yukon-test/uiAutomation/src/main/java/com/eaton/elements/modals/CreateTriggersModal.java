package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.NumericPickerElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateTriggersModal extends BaseModal {

    private DriverExtensions driverExt;
    private DropDownElement type;
    private TrueFalseCheckboxElement usePeakTracking;
    private NumericPickerElement minRestoreOffset;
    
    public CreateTriggersModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        this.driverExt = driverExt;
        WebElement modal = getModal();
        type = new DropDownElement(this.driverExt, "triggerType", modal);
        usePeakTracking = new TrueFalseCheckboxElement(this.driverExt, "usePeak", modal);
        minRestoreOffset = new NumericPickerElement(this.driverExt, "minRestoreOffset", modal);
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
