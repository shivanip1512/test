package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AreaCreatePage extends PageBase { 
    
    private TextEditElement geoName;
    private SwitchBtnYesNoElement state;
    private Button saveBtn;
    private Button cancelBtn;

    public AreaCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        this.driverExt = driverExt;
        requiresLogin = true;
        pageUrl = Urls.CapControl.AREA_CREATE;

        geoName = new TextEditElement(this.driverExt, "description");
        state = new SwitchBtnYesNoElement(this.driverExt, "disabled");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public SwitchBtnYesNoElement getState() {
        return state;
    }   
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
