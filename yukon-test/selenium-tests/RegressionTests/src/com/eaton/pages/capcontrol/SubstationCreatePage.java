package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class SubstationCreatePage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement geoName;
    private TextEditElement mapLocation;
    private TrueFalseCheckboxElement status;
    private Button saveBtn;
    private Button cancelBtn;

    public SubstationCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
        geoName = new TextEditElement(this.driverExt, "geoAreaName");
        mapLocation = new TextEditElement(this.driverExt, "CapControlSubstation.mapLocationID");
        status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public TextEditElement getMapLocation() {
        return mapLocation;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
