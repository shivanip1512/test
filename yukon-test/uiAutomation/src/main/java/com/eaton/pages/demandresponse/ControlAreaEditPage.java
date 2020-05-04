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

public class ControlAreaEditPage extends PageBase {

    private TextEditElement name;
    private DropDownElement controlInterval;
    private DropDownElement minResponseTime;
    private DropDownElement dailyDefaultState;
    private TrueFalseCheckboxElement requireAllTriggersActive;
    private TrueFalseCheckboxElement useOptionControlWindow;

    public ControlAreaEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CONTROL_AREA_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        controlInterval = new DropDownElement(this.driverExt, "controlInterval");
        minResponseTime = new DropDownElement(this.driverExt, "minResponseTime");
        dailyDefaultState = new DropDownElement(this.driverExt, "dailyDefaultState");
        requireAllTriggersActive = new TrueFalseCheckboxElement(this.driverExt, "allTriggersActiveFlag");
        useOptionControlWindow = new TrueFalseCheckboxElement(this.driverExt, "controlWindow");
    }
    
    // General
    public TextEditElement getName() {
        return name;
    }  
    
    public DropDownElement getControlInterval() {
        return controlInterval;
    }
    
    public DropDownElement getMinResponseTime() {
        return minResponseTime;
    }
    
    public DropDownElement getDailyDefaultState() {
        return dailyDefaultState;
    }
    
    public TrueFalseCheckboxElement getRequireAlTriggersActive() {
        return requireAllTriggersActive;
    }
    
    public TrueFalseCheckboxElement getUseOptionControlWindow() {
        return useOptionControlWindow;
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
