package com.eaton.elements.panels;


import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class MeterEventsPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public MeterEventsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        this.driverExt = driverExt;
        this.panel = super.getPanel();
    }
    
    public WebElement getPanel() {
    	return panel;
    }

}
