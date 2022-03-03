package com.eaton.pages.demandresponse;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.CreateTriggersModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class ControlAreaCreatePage extends PageBase {

    public ControlAreaCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CONTROL_AREA_CREATE;
    }
    
    // General
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }  
    
    public DropDownElement getControlInterval() {
        return new DropDownElement(this.driverExt, "controlInterval");
    }
    
    public DropDownElement getMinResponseTime() {
        return new DropDownElement(this.driverExt, "minResponseTime");
    }
    
    public DropDownElement getDailyDefaultState() {
        return new DropDownElement(this.driverExt, "dailyDefaultState");
    }
    
    public TrueFalseCheckboxElement getRequireAlTriggersActive() {
        return new TrueFalseCheckboxElement(this.driverExt, "allTriggersActiveFlag");
    }
    
    public TrueFalseCheckboxElement getUseOptionControlWindow() {
        return new TrueFalseCheckboxElement(this.driverExt, "controlWindow");
    }
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }
    
    private Button getCreateTrigger() {
        return new Button(this.driverExt, "Create");
    }
    
    public CreateTriggersModal showTriggerModal() {
        
        getCreateTrigger().click();
        
        return new CreateTriggersModal(this.driverExt, Optional.of("js-add-triggers"), Optional.empty());
        
    }
    
    public SelectBoxElement getProgramAssignments() {
        Section section = new Section(this.driverExt, "Program Assignments");
                
        return new SelectBoxElement(this.driverExt, section.getSection());        
    }
}
