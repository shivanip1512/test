package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class MeterInfoPanel extends BasePanel {

    private DriverExtensions driverExt;   
    
    public MeterInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
    }

    public Button getEdit() {
        return new Button(this.driverExt, "Edit", getPanel());
    }    
    
    public NameValueTable getTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }
}
