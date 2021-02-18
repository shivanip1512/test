package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
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

    public CapBankEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CAP_BANK_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        address = new TextEditElement(this.driverExt, "location");
        mapLocationId = new TextEditElement(this.driverExt, "CapBank.mapLocationID");
        switchManufacturer = new DropDownElement(this.driverExt, "CapBank.switchManufacture");
        controllerType = new DropDownElement(this.driverExt, "CapBank.controllerType");
        typeOfSwitch = new DropDownElement(this.driverExt, "CapBank.typeOfSwitch");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
        deleteBtn = new Button(this.driverExt, "Delete");
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
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {
        
        getDeleteBtn().click();       
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
}
