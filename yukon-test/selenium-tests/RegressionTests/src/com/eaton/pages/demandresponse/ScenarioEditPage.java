package com.eaton.pages.demandresponse;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class ScenarioEditPage extends PageBase {

    private TextEditElement name;

    public ScenarioEditPage(DriverExtensions driverExt, String pageUrl) {
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
        Section section = new Section(this.driverExt, "Add/Remove Load Programs");
                
        return new SelectBoxElement(this.driverExt, section.getSection());        
    }
}
