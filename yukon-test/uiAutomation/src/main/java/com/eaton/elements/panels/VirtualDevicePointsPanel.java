package com.eaton.elements.panels;

import com.eaton.elements.Button;
import com.eaton.elements.CreateBtnDropDownElement;
import com.eaton.elements.DropDownMultiSelectElement;
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
    
    public Button getFilter() {
        return new Button(this.driverExt, "Filter", getPanel());
    }
    
    public DropDownMultiSelectElement getPointType() {
        return new DropDownMultiSelectElement(this.driverExt, "pointTypes", getPanel());
    }
    
    public CreateBtnDropDownElement getCreateBtn() {
        return new CreateBtnDropDownElement(this.driverExt, getPanel());
    }
}
