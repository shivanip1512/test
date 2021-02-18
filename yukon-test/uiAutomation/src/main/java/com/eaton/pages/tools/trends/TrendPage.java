package com.eaton.pages.tools.trends;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.modals.TrendMarkerModal;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class TrendPage extends PageBase {
    
    private TabElement tab;

    public TrendPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt);
        
        this.pageUrl = pageUrl;
        requiresLogin = true;
        tab = new TabElement(this.driverExt);
    }         
    
    public TabElement getTabElement() {
        return tab;
    }   
    
    /* This is to get the tab link */
    public WebElement getAdditionalOptionsTab() {
        return getTabElement().getTabByName("Additional Options");
    }
    
    public WebElement getSetupTab() {
        return getTabElement().getTabByName("Setup");
    }
    
    /* This is to get the container/section inside the tab */
    public WebElement getAdditionalOptionsTabContainer() {
        return getTabElement().getTabPanelByName("Additional Options");
    }
    
    public WebElement getSetupTabContainer() {
        return getTabElement().getTabPanelByName("Setup");
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General", getSetupTabContainer());
    }
    
    public Section getPointSetupSection() {
        return new Section(this.driverExt, "Point Setup", getSetupTabContainer());
    }
    
    public Section getMarkerSetupSection() {
        return new Section(this.driverExt, "Marker Setup", getAdditionalOptionsTabContainer());
    }   
    
    public WebTable getPointSetupTable() {
        return new WebTable(this.driverExt, "compact-results-table", getPointSetupSection().getSection());
    }
    
    public WebTable getMarkerSetupTable() {
        return new WebTable(this.driverExt, "compact-results-table", getMarkerSetupSection().getSection());
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getGeneralSection().getSection());
    }
    
    private Button getpointSetupAdd() {
        return new Button(this.driverExt, "Add", getPointSetupSection().getSection());
    }
    
    private Button getMarkerSetupAdd() {
        return new Button(this.driverExt, "Add", getMarkerSetupSection().getSection());
    }   
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }
    
    public TrendPointModal showAndWaitAddPointModal() {
        getpointSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-add-point-dialog");
        
        return new TrendPointModal(this.driverExt, Optional.empty(), Optional.of("js-add-point-dialog"));
    }
    
    public TrendPointModal showAndWaitEditPointModal(String modalTitle, int index) {
        WebTableRow row = getPointSetupTable().getDataRowByIndex(index);
        
        row.clickActionIcon(Icons.PENCIL);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(modalTitle);
        
        return new TrendPointModal(this.driverExt, Optional.of(modalTitle), Optional.empty());
    }
    
    public TrendMarkerModal showAndWaitEditMarkerModal(String modalTitle, int index) {                
        WebTableRow row = getMarkerSetupTable().getDataRowByIndex(index);
        
        row.clickActionIcon(Icons.PENCIL);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(modalTitle);
        
        return new TrendMarkerModal(this.driverExt, Optional.of(modalTitle) ,Optional.empty());
    }
    
    public TrendMarkerModal showAndWaitAddMarkerModal() {
        getMarkerSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-add-marker-dialog");
        
        return new TrendMarkerModal(this.driverExt, Optional.empty(), Optional.of("js-add-marker-dialog"));
    }
}
