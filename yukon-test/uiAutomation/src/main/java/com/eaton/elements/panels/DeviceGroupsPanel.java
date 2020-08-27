package com.eaton.elements.panels;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class DeviceGroupsPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public DeviceGroupsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        this.driverExt = driverExt;
        this.panel = initPanel();
    }
    
  //================================================================================
    // Private Functions Section
    //================================================================================
    
    private WebElement initPanel() {
    	WebElement panel = null;
    	try {
    		panel = super.getPanel();
    	} catch(NoSuchElementException e) {
    	}
    	return panel;
    }
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public WebElement getPanel() {
    	return panel;
    }

}
