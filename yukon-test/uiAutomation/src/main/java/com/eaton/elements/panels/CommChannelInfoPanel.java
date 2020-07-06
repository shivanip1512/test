package com.eaton.elements.panels;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;

public class CommChannelInfoPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public CommChannelInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
        this.panel = getPanel();
    }
    
    public String getPanelNameText() {
        return getPanelName();
    }

    public Button getEdit() {
        return new Button(this.driverExt, "Edit", this.panel);
    }    
}
