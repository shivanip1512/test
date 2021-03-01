package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationBusEditPage extends PageBase {
    
    private TextEditElement name;
    //private TrueFalseCheckboxElement status;
    private TextEditElement geoName;
    private TextEditElement mapLocationId;
    //TODO add Parent
    //TODO add Volt Reduction Control Point link and modal
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public SubstationBusEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_BUS_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        //status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        geoName = new TextEditElement(this.driverExt, "geoAreaName");
        mapLocationId = new TextEditElement(this.driverExt, "capControlSubstationBus.mapLocationID");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
        deleteBtn = new Button(this.driverExt, "Delete");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
//    public TrueFalseCheckboxElement getStatus() {
//        return status;
//    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public TextEditElement getMapLocationId() {
        return mapLocationId;
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
