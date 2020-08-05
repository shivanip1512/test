package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.PickerElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AreaCreatePage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement geoName;
    private SwitchBtnYesNoElement state;
    private PickerElement voltReduction;
    private Button saveBtn;
    private Button cancelBtn;

    public AreaCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.AREA_CREATE;

        name = new TextEditElement(driverExt, "name");
        geoName = new TextEditElement(driverExt, "description");
        state = new SwitchBtnYesNoElement(driverExt, "disabled");
        voltReduction = new PickerElement(driverExt, Optional.of("voltReduction"), Optional.empty());
        saveBtn = new Button(driverExt, "Save");
        cancelBtn = new Button(driverExt, "Cancel");
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public SwitchBtnYesNoElement getState() {
        return state;
    }
    
    public PickerElement getVoltReduction() {
        return voltReduction;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
