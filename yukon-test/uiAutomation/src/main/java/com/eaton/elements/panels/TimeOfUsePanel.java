package com.eaton.elements.panels;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.NameValueTable;
import com.eaton.framework.DriverExtensions;

public class TimeOfUsePanel extends BasePanel {

    private DriverExtensions driverExt;
    
    public TimeOfUsePanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
    }
    
    public Button getReadBtn() {
        return new Button(this.driverExt, "Read", getPanel());
    }
    
    public NameValueTable getUsageRateATable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.of(0));
    }
    
    public NameValueTable getUsageRateBTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.of(1));
    }
    
    public NameValueTable getUsageRateCTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.of(2));
    }
    
    public NameValueTable getUsageRateDTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.of(3));
    }
    
}
