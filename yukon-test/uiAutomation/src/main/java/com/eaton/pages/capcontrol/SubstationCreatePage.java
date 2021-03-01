package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SubstationCreatePage extends PageBase {
    
    public SubstationCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.SUBSTATION_CREATE;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }
    
    public TextEditElement getGeoName() {
        return new TextEditElement(this.driverExt, "geoAreaName");
    }
    
    public TextEditElement getMapLocation() {
        return new TextEditElement(this.driverExt, "CapControlSubstation.mapLocationID");
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled");
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }    
}
