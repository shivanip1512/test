package com.eaton.elements.panels;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class WiFiConnectionPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    private Button queryButton;
    private WebTable pointsTable;
    private String commStatusLabel;
    private String commStatusValue;
    private String rssiLabel;
    private String rssiValue;
    
    public WiFiConnectionPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        
        this.driverExt = driverExt;
        this.panel = initPanel();
        if(panel == null) {
        	queryButton = null;
        	pointsTable = null;
        	commStatusLabel = null;
        	rssiLabel = null;
        	commStatusValue = null;
        	rssiValue = null;
        	
        } else {
	        this.queryButton = new Button(this.driverExt, "Query", this.panel);
	        this.pointsTable = new WebTable(driverExt, "name-value-table", panel);
	        List<String> labelEntries = pointsTable.getDataRowsTextByCellIndex(LABEL_INDEX);
	        this.commStatusLabel = labelEntries.get(0);
	        this.rssiLabel = labelEntries.get(1);
	        
	        List<String> valueEntries = pointsTable.getDataRowsTextByCellIndex(VALUE_INDEX);
	        this.commStatusValue = valueEntries.get(0);
	        this.rssiValue = valueEntries.get(1);
        }
    }
    
    //================================================================================
    // Private Functions Section
    //================================================================================
    
    private WebElement initPanel() {
    	WebElement panel = null;
    	try {
    		panel = super.getPanel();
    	} catch(NoSuchElementException e) {
    	}
    	return panel;
    }
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public WebElement getPanel() {
    	return panel;
    }

    public Button getQueryButton() {
        return queryButton;
    }
    
    public WebTable getPointsTable() {
    	return pointsTable;
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
