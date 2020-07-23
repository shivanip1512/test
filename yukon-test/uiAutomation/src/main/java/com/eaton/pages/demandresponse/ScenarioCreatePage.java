package com.eaton.pages.demandresponse;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class ScenarioCreatePage extends PageBase {

    public ScenarioCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.SCENARIO_CREATE;
    }
    
    // General
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }          
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }       
    
    public SelectBoxElement getLoadProgramAssignments() {
        Section section = new Section(this.driverExt, "Add/Remove Load Programs");
                
        return new SelectBoxElement(this.driverExt, section.getSection());        
    }
}
