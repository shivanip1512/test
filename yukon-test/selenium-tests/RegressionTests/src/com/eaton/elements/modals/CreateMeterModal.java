package com.eaton.elements.modals;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.DropDownSearchElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;

public class CreateMeterModal extends BaseModal {

    private WebDriver driver;
    private static final String PARENT_NAME = "contentPopup";
    
    public CreateMeterModal(WebDriver driver, String modalName) {
        super(driver, modalName);
        
        this.driver = driver;
    }

    public DropDownSearchElement getType() {
        return new DropDownSearchElement(this.driver, "meter_type_chosen", PARENT_NAME);
    }
    
    public TextEditElement getdeviceName() {
        return  new TextEditElement(this.driver, "name", PARENT_NAME);
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(this.driver, "meterNumber", PARENT_NAME);
    }
    
    public TextEditElement getPhycialAddress() {
        return new TextEditElement(this.driver, "address", PARENT_NAME);
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(this.driver, "routeId", PARENT_NAME);
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driver, "serialNumber", PARENT_NAME);
    }
    
    public TextEditElement getManufacturer() {
        return new TextEditElement(this.driver, "manufacturer", PARENT_NAME);
    }
    
    public TextEditElement getModel() {
        return new TextEditElement(this.driver, "model", PARENT_NAME);
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driver, "disabled", PARENT_NAME);
    }        
}
