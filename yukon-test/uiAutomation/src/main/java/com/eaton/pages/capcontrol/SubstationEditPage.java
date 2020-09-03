package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationEditPage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement geoName;
    private TextEditElement mapLocation;
    private TrueFalseCheckboxElement status;
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public SubstationEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_EDIT + id + Urls.EDIT;
        
        name = new TextEditElement(this.driverExt, "name");
        geoName = new TextEditElement(this.driverExt, "geoAreaName");
        mapLocation = new TextEditElement(this.driverExt, "CapControlSubstation.mapLocationID");
        status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
        deleteBtn = new Button(this.driverExt, "Delete");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public TextEditElement getMapLocation() {
        return mapLocation;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
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
