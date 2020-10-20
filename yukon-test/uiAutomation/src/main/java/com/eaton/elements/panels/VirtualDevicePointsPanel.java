package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class VirtualDevicePointsPanel extends BasePanel {

	private DriverExtensions driverExt; 

	public VirtualDevicePointsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);    
    }   
	
	public WebTable getTable() {
		return new WebTable(this.driverExt, "compact-results-table", getPanel());
	}
}
