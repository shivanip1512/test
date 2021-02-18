package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class RegulatorEditPage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement description;
    private DropDownElement type;
    private DropDownElement configuration;
    private TrueFalseCheckboxElement status;
    private WebTable table;

    public RegulatorEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.REGULATOR_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        description = new TextEditElement(this.driverExt, "description");
        type = new DropDownElement(this.driverExt, "type");
        configuration = new DropDownElement(this.driverExt, "configId");
        status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        table = new WebTable(this.driverExt, "compact-results-table");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getDescription() {
        return description;
    }
    
    public DropDownElement getType() {
        return type;
    }
    
    public DropDownElement getConfiguration() {
        return configuration;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public WebTable getAttributeMappingsTable() {
        return table;
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }   
    
    public Button getDeleteBtn() {
        return new Button(this.driverExt, "Delete");
    }
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {
        
        getDeleteBtn().click();       
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
}
