package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class VirtualDeviceInfoPanel extends BasePanel {
	
	private DriverExtensions driverExt;
	
	public VirtualDeviceInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);  
    }    
	
	public NameValueTable getNameStatusTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }
}
