package com.eaton.elements.modals;
import java.util.Optional;

import com.eaton.elements.DatePickerElement;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.framework.DriverExtensions;


public class ResetPeakModal extends BaseModal {
    
    public ResetPeakModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
    }
    
    //Trend
    public DropDownElement getResetPeakTo() {
        return new DropDownElement(this.driverExt, "resetPeakDuration");
    }
    
    public DatePickerElement getDate() {
        return new DatePickerElement(this.driverExt, "startDate");
    }
    
    public RadioButtonElement getResetPeakForAllTrends() {
        return new RadioButtonElement(this.driverExt, "resetPeakForAllTrends", getModal());
    }
}
