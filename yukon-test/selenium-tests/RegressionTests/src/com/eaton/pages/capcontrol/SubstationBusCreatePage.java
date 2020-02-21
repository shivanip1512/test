package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class SubstationBusCreatePage extends PageBase {
    
    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TextEditElement geoName;
    private TextEditElement mapLocationId;
    //TODO add Parent
    //TODO add Volt Reduction Control Point link and modal
    private Button saveBtn;
    private Button cancelBtn;

    public SubstationBusCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
        status = new TrueFalseCheckboxElement(this.driverExt, "disabled");
        geoName = new TextEditElement(this.driverExt, "geoAreaName");
        mapLocationId = new TextEditElement(this.driverExt, "capControlSubstationBus.mapLocationID");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public TextEditElement getMapLocationId() {
        return mapLocationId;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
