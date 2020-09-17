package com.eaton.elements.panels;

import java.util.List;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class MeterInfoPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebTable pointsTable;
    private List<String> labelEntries;
    private List<String> valueEntries;
    
    public MeterInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        
        this.pointsTable = new WebTable(driverExt, "name-value-table", getPanel());
        this.labelEntries = pointsTable.getDataRowsTextByCellIndex(LABEL_INDEX);
        this.valueEntries = pointsTable.getDataRowsTextByCellIndex(VALUE_INDEX);
    }

    //================================================================================
    // Private Functions Section
    //================================================================================
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================

    public Button getEdit() {
        return new Button(this.driverExt, "Edit", getPanel());
    }
    
    public List<String> getLabelEntries() {
    	return labelEntries;
    }
    
    public List<String> getValueEntries() {
    	return valueEntries;
    }
}
