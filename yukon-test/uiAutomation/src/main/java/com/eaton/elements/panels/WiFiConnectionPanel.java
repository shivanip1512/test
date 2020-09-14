package com.eaton.elements.panels;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class WiFiConnectionPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebTable pointsTable;
    private List<String> labelEntries;
    private List<String> valueEntries;
    private String commStatusLabel;
    private String commStatusValue;
    private String rssiLabel;
    private String rssiValue;
    
    public WiFiConnectionPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        
        this.driverExt = driverExt;
        WebElement panel = getPanel();
       
        this.pointsTable = new WebTable(driverExt, "name-value-table", panel);
        this.labelEntries = pointsTable.getDataRowsTextByCellIndex(LABEL_INDEX);
        this.commStatusLabel = labelEntries.get(0);
        this.rssiLabel = labelEntries.get(1);
        
        this.valueEntries = pointsTable.getDataRowsTextByCellIndex(VALUE_INDEX);
        this.commStatusValue = valueEntries.get(0);
        this.rssiValue = valueEntries.get(1);
    }
    
    //================================================================================
    // Private Functions Section
    //================================================================================ 
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================

    public Button getQueryButton() {
        return new Button(this.driverExt, "Query", getPanel());
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
    
    public String getCommStatusLabel() {
    	return commStatusLabel;
    }
    
    public String getCommStatusValue() {
    	return commStatusValue;
    }
    
    public String getRSSILabel() {
    	return rssiLabel;
    }
    
    public String getRSSIValue() {
    	return rssiValue;
    }
}
