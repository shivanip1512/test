package com.eaton.elements.panels;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class TimeOfUsePanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    private WebTable pointsTableA;
    private WebTable pointsTableB;
    private WebTable pointsTableC;
    private WebTable pointsTableD;
    private List<String> labelEntries;
    private List<String> valueEntries;
    
    public TimeOfUsePanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        final int LABEL_INDEX = 1;
        final int VALUE_INDEX = 2;
        this.driverExt = driverExt;
        this.panel = super.getPanel();
        
        this.pointsTableA = new WebTable(driverExt, "name-value-table:nth-of-type(1)", panel);
        this.labelEntries = pointsTableA.getDataRowsTextByCellIndex(LABEL_INDEX);
        this.valueEntries = pointsTableA.getDataRowsTextByCellIndex(VALUE_INDEX);
        this.pointsTableB = new WebTable(driverExt, "name-value-table:nth-of-type(2)", panel);
        this.labelEntries.addAll(pointsTableB.getDataRowsTextByCellIndex(LABEL_INDEX));
        this.valueEntries.addAll(pointsTableB.getDataRowsTextByCellIndex(VALUE_INDEX));
        this.pointsTableC = new WebTable(driverExt, "name-value-table:nth-of-type(3)", panel);
        this.labelEntries.addAll(pointsTableC.getDataRowsTextByCellIndex(LABEL_INDEX));
        this.valueEntries.addAll(pointsTableC.getDataRowsTextByCellIndex(VALUE_INDEX));
        this.pointsTableD = new WebTable(driverExt, "name-value-table:nth-of-type(4)", panel);
        this.labelEntries.addAll(pointsTableD.getDataRowsTextByCellIndex(LABEL_INDEX));
        this.valueEntries.addAll(pointsTableD.getDataRowsTextByCellIndex(VALUE_INDEX));
    }
    
    //================================================================================
    // Private Functions Section
    //================================================================================
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public WebElement getPanel() {
    	return panel;
    }
    
    public WebTable getPointsTableA() {
    	return pointsTableA;
    }
    
    public WebTable getPointsTableB() {
    	return pointsTableB;
    }
    
    public WebTable getPointsTableC() {
    	return pointsTableC;
    }
    
    public WebTable getPointsTableD() {
    	return pointsTableD;
    }
    
    public List<String> getLabelEntries() {
    	return labelEntries;
    }
    
    public List<String> getValueEntries() {
    	return valueEntries;
    }

}
