package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.NumericPickerElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateTriggersModal extends BaseModal {

    private WebDriver driver;
    private DropDownElement type;
    private TrueFalseCheckboxElement usePeakTracking;
    private NumericPickerElement minRestoreOffset;
    
    public CreateTriggersModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
        type = new DropDownElement(this.driver, "triggerType", null);
        usePeakTracking = new TrueFalseCheckboxElement(this.driver, "usePeak", null);
        minRestoreOffset = new NumericPickerElement(this.driver, "minRestoreOffset", null);
    }  
    
    //TODO Trigger Identification and Threshold Point Settings elements do not have a unique way to select them
    //TODO Save and Cancel buttons do not have a unique way to select them
    
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
