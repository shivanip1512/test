package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class EditMeterModal extends BaseModal {

    private WebDriver driver;
    
    public EditMeterModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
    }
    
    public TextEditElement getdeviceName() {
        return new TextEditElement(this.driver, "name", getModal());
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(this.driver, "meterNumber", getModal());
    }
    
    public TextEditElement getPhycialAddress() {
        return  new TextEditElement(this.driver, "address", getModal());
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(this.driver, "routeId", getModal());
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driver, "serialNumber", getModal());
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driver, "disabled", getModal());
    }
}
