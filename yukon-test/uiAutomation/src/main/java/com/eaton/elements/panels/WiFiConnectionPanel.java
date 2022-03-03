package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class WiFiConnectionPanel extends BasePanel {

    private DriverExtensions driverExt;

    public WiFiConnectionPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
    }    

    public Button getQueryButton() {
        return new Button(this.driverExt, "Query", getPanel());
    }
    
    
    public NameValueTable getTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }
}
