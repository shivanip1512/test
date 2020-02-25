package com.eaton.pages.demandresponse;

import com.eaton.elements.Button;
import com.eaton.elements.Sections;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class ScenarioCreatePage extends PageBase {

    private TextEditElement name;

    public ScenarioCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
    }
    
    // General
    public TextEditElement getName() {
        return name;
    }          
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }       
    
    public SelectBoxElement getLoadProgramAssignments() {
        Sections section = new Sections(this.driverExt, "js-control-scenario-form");
                
        return new SelectBoxElement(this.driverExt, section.getSectionByName("Add/Remove Load Programs"));        
    }
}
