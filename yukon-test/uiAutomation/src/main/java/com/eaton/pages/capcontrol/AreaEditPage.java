package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AreaEditPage extends PageBase {
    
    private TextEditElement name;
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public AreaEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.AREA_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
        deleteBtn = new Button(this.driverExt, "Delete");
    }
    
    public TextEditElement getName() {
        return name;
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
