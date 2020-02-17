package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class CapBankEditPage  extends PageBase {
    
    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TextEditElement address;
    private TextEditElement mapLocationId;
    private DropDownElement switchManufacturer;
    private DropDownElement controllerType;
    private DropDownElement typeOfSwitch;
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public CapBankEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        status = new TrueFalseCheckboxElement(this.driver, "disabled");
        address = new TextEditElement(this.driver, "location");
        mapLocationId = new TextEditElement(this.driver, "CapBank.mapLocationID");
        switchManufacturer = new DropDownElement(this.driver, "CapBank.switchManufacture");
        controllerType = new DropDownElement(this.driver, "CapBank.controllerType");
        typeOfSwitch = new DropDownElement(this.driver, "CapBank.typeOfSwitch");
        saveBtn = new Button(this.driver, "Save");
        cancelBtn = new Button(this.driver, "Cancel");
        deleteBtn = new Button(this.driver, "Delete");
    }

    public String getPageTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public TextEditElement getAddress() {
        return address;
    }
    
    public TextEditElement getMapLocationId() {
        return mapLocationId;
    }
    
    public DropDownElement getSwitchManufacturer() {
        return switchManufacturer;
    }
    
    public DropDownElement getControllerType() {
        return controllerType;
    }
    
    public DropDownElement getTypeOfSwitch() {
        return typeOfSwitch;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
    
    public Button getDeleteBtn() {
        return deleteBtn;
    }
}
