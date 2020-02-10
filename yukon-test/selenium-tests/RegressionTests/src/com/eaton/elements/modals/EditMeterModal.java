package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;

public class EditMeterModal extends BaseModal {

    private WebDriver driver;
    private TextEditElement deviceName;
    private TextEditElement meterNumber;
    private TextEditElement physicalAddress;
    private DropDownElement route;
    private TextEditElement serialNumber;
    //TODO need to work with dev on how switchbutton works
    //private SwitchButton status;
    
    public EditMeterModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
    }
    
    public TextEditElement getdeviceName() {
        deviceName = new TextEditElement(driver, "name", null);
        
        return deviceName;
    }
    
    public TextEditElement getMeterNumber() {
        meterNumber = new TextEditElement(driver, "meterNumber", null);
        
        return meterNumber;
    }
    
    public TextEditElement getPhycialAddress() {
        physicalAddress = new TextEditElement(driver, "address", null);
        
        return physicalAddress;
    }
    
    public DropDownElement getRoute() {
        route = new DropDownElement(driver, "routeId", null);
        
        return route;
    }
    
    public TextEditElement getSerialNumber() {
        serialNumber = new TextEditElement(driver, "serialNumber", null);
        
        return serialNumber;
    }
}
