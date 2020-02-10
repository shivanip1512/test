package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;

public class CreateMeterModal extends BaseModal {

    private WebDriver driver;
    //TODO need to work with dev on how switchbutton works
    //private SwitchButton status;
    
    public CreateMeterModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
    }

    public DropDownElement getType() {
        return new DropDownElement(this.driver, "type", null);
    }
    
    public TextEditElement getdeviceName() {
        return  new TextEditElement(driver, "name", null);
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(driver, "meterNumber", null);
    }
    
    public TextEditElement getPhycialAddress() {
        return new TextEditElement(driver, "address", null);
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(driver, "routeId", null);
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(driver, "serialNumber", null);
    }
}
