package com.eaton.pages.tools.trends;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.TrendAddMarkerModal;
import com.eaton.elements.modals.TrendAddPointModal;
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
    
    public WebElement getAdditionalOptionsTab() {
        return getTabElement().getTabPanelByName("Additional Options");
    }
    
    public WebElement getSetupTab() {
        return getTabElement().getTabPanelByName("Setup");
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General", getSetupTab());
    }
    
    public Section getPointSetupSection() {
        return new Section(this.driverExt, "Point Setup", getSetupTab());
    }
    
    public Section getMarkerSetupSection() {
        return new Section(this.driverExt, "Marker Setup", getAdditionalOptionsTab());
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
    
    public TrendAddPointModal showAndWaitAddPointModal() {
        getpointSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-add-point-dialog");
        
        return new TrendAddPointModal(this.driverExt, Optional.empty(), Optional.of("js-add-point-dialog"));
    }
    
    public TrendAddMarkerModal showAndWaitAddMarkerModal() {
        getMarkerSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-add-marker-dialog");
        
        return new TrendAddMarkerModal(this.driverExt, Optional.empty(), Optional.of("js-add-marker-dialog"));
    }
}
