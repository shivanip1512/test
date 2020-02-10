package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.pages.PageBase;

public class CbcCreatePage  extends PageBase {
    
    private TextEditElement name;
    private DropDownElement type;
    private TextEditElement serialNumber;

    public CbcCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(driver, "name", null);
        type = new DropDownElement(driver, "paoType", null);                
        serialNumber = new TextEditElement(driver, "deviceCBC.serialNumber", null);
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public Button getSaveBtn() {
        return new Button(driver, "Save", null);
    }
    
    public Button getCancelBtn() {
        return new Button(driver, "Cancel", null);
    }  
    
    public DropDownElement getType() {
        return type;
    } 
    
    public TextEditElement getSerialNumber() {
        return serialNumber;
    }
    
    public TextEditElement getMasterAddress() {
        return new TextEditElement(driver, "deviceAddress.masterAddress", null);
    }    
}
