package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class TrendAddMarkerModal extends BaseModal {
    
    private String modalTitle;
    private String describedBy;

    public TrendAddMarkerModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        if(modalTitle.isPresent()) {
            this.modalTitle = modalTitle.get();
        }
        
        if(describedBy.isPresent()) {
            this.describedBy = describedBy.get();
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
    
    public String getColor() {
    	return getModal().findElement(By.cssSelector("table tr .value .sp-preview-inner")).getAttribute("style");
    }
}
