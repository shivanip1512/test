package com.eaton.pages.tools.trends;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.ButtonByClass;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.MarkerModal;
import com.eaton.elements.modals.PointModal;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class TrendEditPage extends PageBase {
    
    private TabElement tab;

    public TrendEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS + id + Urls.EDIT;
        tab = new TabElement(this.driverExt);
    }
    
    public TrendEditPage(DriverExtensions driverExt) {
        super(driverExt);
    }        
    
    public TabElement getTabElement() {
        return tab;
    }
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
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
    
    public WebTable getMarkerSetupTable() {
        return new WebTable(this.driverExt, "compact-results-table", getMarkerSetupSection().getSection());
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getGeneralSection().getSection());
    }
    
    public Button getpointSetupAdd() {
        return new Button(this.driverExt, "Add", getPointSetupSection().getSection());
    }
    
    public Button getMarkerSetupAdd() {
        return new Button(this.driverExt, "Add", getMarkerSetupSection().getSection());
    }
    
    public ButtonByClass getMarkerSetupEditMarker() {
        return new ButtonByClass(this.driverExt, "js-marker", getMarkerSetupSection().getSection());
    }
    
    public ButtonByClass getPointSetupEditPoint() {
        return new ButtonByClass(this.driverExt, "js-edit-point", getPointSetupSection().getSection());
    }
    
    public ButtonByClass getMarkerSetupRemoveMarker() {
        return new ButtonByClass(this.driverExt, "js-remove", getMarkerSetupSection().getSection());
    }
    
    public ButtonByClass getPointSetupRemovePoint() {
        return new ButtonByClass(this.driverExt, "js-remove", getPointSetupSection().getSection());
    }
    
    public PointModal showAndWaitAddPointModal() {
        getpointSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-add-point-dialog");
        
        return new PointModal(this.driverExt, Optional.empty(), Optional.of("js-add-point-dialog"));
    }
    
    public MarkerModal showAndWaitAddMarkerModal() {
        getMarkerSetupAdd().click();
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-add-marker-dialog");
        
        return new MarkerModal(this.driverExt, Optional.empty(), Optional.of("js-add-marker-dialog"));
    }
}
