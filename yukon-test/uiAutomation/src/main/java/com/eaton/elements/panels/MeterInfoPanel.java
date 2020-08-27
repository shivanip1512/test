package com.eaton.elements.panels;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class MeterInfoPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    private WebTable pointsTable;
    private List<String> labelEntries;
    private List<String> valueEntries;
    
    public MeterInfoPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        
        this.driverExt = driverExt;
        this.panel = initPanel();
        if(panel == null) {
        	pointsTable = null;
        	labelEntries = null;
        	valueEntries = null;
        } else {
	        this.pointsTable = new WebTable(driverExt, "name-value-table", panel);
	        this.labelEntries = pointsTable.getDataRowsTextByCellIndex(LABEL_INDEX);
	        this.valueEntries = pointsTable.getDataRowsTextByCellIndex(VALUE_INDEX);
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

    public Button getEdit() {
        return new Button(this.driverExt, "Edit", this.panel);
    }
    
    public List<String> getLabelEntries() {
    	return labelEntries;
    }
    
    public List<String> getValueEntries() {
    	return valueEntries;
    }
}
