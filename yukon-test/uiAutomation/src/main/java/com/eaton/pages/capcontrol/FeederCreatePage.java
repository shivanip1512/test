package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class FeederCreatePage extends PageBase {
    
    public FeederCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.FEEDER_CREATE;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled");
    }
    
    public TextEditElement getMapLocationId() {
        return new TextEditElement(this.driverExt, "capControlFeeder.mapLocationID");
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }    
}
