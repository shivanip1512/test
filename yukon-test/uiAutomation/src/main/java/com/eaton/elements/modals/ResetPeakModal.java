package com.eaton.elements.modals;
import java.util.Optional;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TimePickerElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;


public class ResetPeakModal extends BaseModal {
    
    private DriverExtensions driverExt; 
    
    public ResetPeakModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        this.driverExt = driverExt;        
    }
    
    //Trend
    public DropDownElement getResetPeakTo() {
        return new DropDownElement(this.driverExt, "resetPeakDuration");
    }
    
    public TimePickerElement getDate() {
        return new TimePickerElement(this.driverExt, "startDate");
    }
    
    public RadioButtonElement getResetPeakForAllTrends() {
        return new RadioButtonElement(this.driverExt, "resetPeakForAllTrends", getModal());
    }
}
