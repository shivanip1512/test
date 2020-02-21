package com.eaton.elements.panels;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;

public class MeterInfoPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public MeterInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
        this.panel = getPanel();
    }

    public Button getEdit() {
        return new Button(this.driverExt, "Edit", this.panel);
    }    
}
