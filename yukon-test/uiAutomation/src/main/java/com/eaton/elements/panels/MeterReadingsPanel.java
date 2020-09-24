package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class MeterReadingsPanel extends BasePanel {

    private DriverExtensions driverExt;
    
    public MeterReadingsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
    }
   
    public Button getEdit() {
        return new Button(this.driverExt, "Edit", getPanel());
    }    
    
    public NameValueTable getTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }
}
