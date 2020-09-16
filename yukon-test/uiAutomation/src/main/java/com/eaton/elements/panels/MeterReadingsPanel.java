package com.eaton.elements.panels;

import java.util.List;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class MeterReadingsPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebTable pointsTable;
    private List<String> labelEntries;
    private List<String> valueEntries;
    
    public MeterReadingsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        
        this.driverExt = driverExt;
        WebElement panel = getPanel();
        
        this.pointsTable = new WebTable(driverExt, "name-value-table", panel);
        this.labelEntries = pointsTable.getDataRowsTextByCellIndex(LABEL_INDEX);
        
        this.valueEntries = pointsTable.getDataRowsTextByCellIndex(VALUE_INDEX); 
    }
   
    
    //================================================================================
    // Private Functions Section
    //================================================================================
    
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================

    public Button getReadButton() {
        return new Button(driverExt, "Read", getPanel());
    }
    
    public WebTable getPointsTable() {
    	return pointsTable;
    }
    
    public List<String> getLabelEntries() {
    	return labelEntries;
    }
    
    public List<String> getValueEntries() {
    	return valueEntries;
    }
}
