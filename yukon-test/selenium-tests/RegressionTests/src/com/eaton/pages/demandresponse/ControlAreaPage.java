package com.eaton.pages.demandresponse;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TimePickerElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class ControlAreaPage extends PageBase {

    private TextEditElement name;
    private DropDownElement controlInterval;
    private DropDownElement minResponseTime;
    private DropDownElement dailyDefaultState;
    private TrueFalseCheckboxElement requireAllTriggers;
    
    private static final String EDITCONTROLAREAURL = "";
    private static final String COPYCONTROLAREAURL = "";
    private static final String DELETECONTROLAREAURL = "";

    public ControlAreaPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(driverExt, "name");
        controlInterval = new DropDownElement(driverExt, "controlInterval");
        minResponseTime = new DropDownElement(driverExt, "minResponseTime");
        dailyDefaultState = new DropDownElement(driverExt, "dailyDefaultState");
        requireAllTriggers = new TrueFalseCheckboxElement(driverExt, "allTriggersActiveFlag");
    }
    
    public String getEditUrl() {
        return EDITCONTROLAREAURL;
    }
    
    public String getCopyUrl() {
        return COPYCONTROLAREAURL;
    }
    
    public String getDeleteUrl() {
        return DELETECONTROLAREAURL;
    }
    
    //General
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
    
    public TrueFalseCheckboxElement getRequireAllTriggersActive() {
        return requireAllTriggers;
    }
    
    //Triggers
    //TODO Need unique way to select the list of triggers linked to the control area
    public Button getCreateTrigger() {
        return new Button(driverExt, "Create");
    }
    
    //TODO add code to open the create trigger modal after clicking the button
    
    //Optional Control Window
    public TrueFalseCheckboxElement getUseOptionalControlWindow() {
        return new TrueFalseCheckboxElement(driverExt, "controlWindow");
    }
    
    public TimePickerElement getStartTime() {
        return new TimePickerElement(driverExt, "dailyStartTime");
    }
    
    public TimePickerElement getStopTime() {
        return new TimePickerElement(driverExt, "dailyStopTime");
    }
    
    //Program Assignments
    //TODO no unique way to select the assignment element
    
    public Button getSave() {
        return new Button(driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(driverExt, "Cancel");
    }
}