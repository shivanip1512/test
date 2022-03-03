package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class DisconnectPanel extends BasePanel {

    private DriverExtensions driverExt;
    
    public DisconnectPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
    }
    
    public Button getDisconnectBtn() {
        return new Button(this.driverExt, "Disconnect", getPanel());
    }
    
    public Button getConnectBtn() {
        return new Button(this.driverExt, "Connect", getPanel());
    }
    
    public Button getQueryBtn() {
        return new Button(this.driverExt, "Query", getPanel());
    }
    
    public NameValueTable getTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }    
}
