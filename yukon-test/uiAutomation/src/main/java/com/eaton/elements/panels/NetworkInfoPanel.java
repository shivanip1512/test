package com.eaton.elements.panels;


import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class NetworkInfoPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public NetworkInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        this.driverExt = driverExt;
        this.panel = super.getPanel();
    }
    
    //================================================================================
    // Private Functions Section
    //================================================================================
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public WebElement getPanel() {
    	return panel;
    }

}
