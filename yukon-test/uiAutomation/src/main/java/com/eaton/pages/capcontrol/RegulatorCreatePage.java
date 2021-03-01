package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class RegulatorCreatePage extends PageBase {
    
    public RegulatorCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.REGULATOR_CREATE;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }
    
    public TextEditElement getDescription() {
        return new TextEditElement(this.driverExt, "description");
    }
    
    public DropDownElement getType() {
        return new DropDownElement(this.driverExt, "type");
    }
    
    public DropDownElement getConfiguration() {
        return new DropDownElement(this.driverExt, "configId");
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled");
    }
    
    public WebTable getAttributeMappingsTable() {
        return new WebTable(this.driverExt, "compact-results-table");
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }    
}
