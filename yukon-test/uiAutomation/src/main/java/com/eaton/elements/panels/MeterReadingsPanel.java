package com.eaton.elements.panels;

import java.util.List;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class MeterReadingsPanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebTable pointsTable;
    
    public MeterReadingsPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
        WebElement panel = getPanel();
        
        this.pointsTable = new WebTable(driverExt, "name-value-table", panel);
    }
   
    public Button getReadButton() {
        return new Button(driverExt, "Read", getPanel());
    }
    
    public WebTable getPointsTable() {
    	return pointsTable;
    }
    
    public List<String> getLabels() {
    	return pointsTable.getDataRowsTextByCellIndex(1);
    }
    
    public List<String> getValues() {
    	return pointsTable.getDataRowsTextByCellIndex(2);
    }
}
