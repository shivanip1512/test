package com.eaton.pages.trend;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class TrendEditPage extends PageBase {

    Section section;
    
    public TrendEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS + id + Urls.EDIT;
    }
    
    public TrendEditPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }  
    
    public Button getAddButton() {
        return new Button(this.driverExt, "Add");
    }
    
    public TabElement getTabElement() {
        return new TabElement(this.driverExt);
    }
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }
    
    public WebElement getAddPoint() {
        section = new Section(driverExt, "Point Setup");
        return section.getSection().findElement(By.cssSelector(".action-area .js-add-point"));
    }
    
    public WebElement getAddMarker() {
        section = new Section(driverExt, "Marker Setup");
        return section.getSection().findElement(By.cssSelector(".action-area .js-add-marker"));
    }
    
    public WebElement getEditPoint() {
        return this.driverExt.findElement(By.cssSelector(".js-edit-point"),  Optional.empty());
    }
    
    public WebElement getRemovePoint() {
        section = new Section(driverExt, "Point Setup");
        return section.getSection().findElement(By.cssSelector(".js-remove"));
    }
    
    public WebElement getEditMarker() {
        return this.driverExt.findElement(By.cssSelector(".js-marker"),  Optional.empty());
    }
    
    public WebElement getRemoveMarker() {
        section = new Section(driverExt, "Point Setup");
        return section.getSection().findElement(By.cssSelector(".js-remove"));
    }
    
}
