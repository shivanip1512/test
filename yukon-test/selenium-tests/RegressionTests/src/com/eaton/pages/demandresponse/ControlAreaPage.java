package com.eaton.pages.demandresponse;

import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TimePickerElement;
import com.eaton.elements.TrueFalseCheckboxElement;
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

    public ControlAreaPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(driver, "name", null);
        controlInterval = new DropDownElement(driver, "controlInterval", null);
        minResponseTime = new DropDownElement(driver, "minResponseTime", null);
        dailyDefaultState = new DropDownElement(driver, "dailyDefaultState", null);
        requireAllTriggers = new TrueFalseCheckboxElement(driver, "allTriggersActiveFlag", null);
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
        return new Button(driver, "Create", null);
    }
    
    //TODO add code to open the create trigger modal after clicking the button
    
    //Optional Control Window
    public TrueFalseCheckboxElement getUseOptionalControlWindow() {
        return new TrueFalseCheckboxElement(driver, "controlWindow", null);
    }
    
    public TimePickerElement getStartTime() {
        return new TimePickerElement(driver, "dailyStartTime");
    }
    
    public TimePickerElement getStopTime() {
        return new TimePickerElement(driver, "dailyStopTime");
    }
    
    //Program Assignments
    //TODO no unique way to select the assignment element
    
    public Button getSave() {
        return new Button(driver, "Save", null);
    }
    
    public Button getCancel() {
        return new Button(driver, "Cancel", null);
    }
}