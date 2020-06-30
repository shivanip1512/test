package com.eaton.elements.tabs;


import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class TabElement {

    private DriverExtensions driverExt;

    public TabElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;        
    }    
    
    private WebElement getTabContainer() {
        return this.driverExt.findElement(By.cssSelector(".tabbed-container"), Optional.empty());
    }
    
    private List<WebElement> getTabs() {
        return getTabContainer().findElements(By.cssSelector(".ui-tabs-tab"));
    }
    
    public WebElement getTabPanel() {
       WebElement tabContainer = getTabContainer();
       
       return tabContainer.findElement(By.cssSelector(".ui-tabs-panel[aria-hidden='false']"));
    }
    
    public void clickTab(String tabName) {
        List<WebElement> list = getTabs();
        
        for (WebElement element : list) {
            String tab = element.findElement(By.cssSelector("a")).getText();
            if(tab.equals(tabName)) {
                element.click();
                return;
            }
        }
    }
    
    public Boolean getTabPresence(String tabName) {
        List<WebElement> list = getTabs();
        Boolean tabPresenceFlag = false;
        
        for (WebElement element : list) {
            String tab = element.findElement(By.cssSelector("a")).getText();
            if(tab.equals(tabName)) {
                tabPresenceFlag = true;
                break;
            }
        }
        return tabPresenceFlag;
    }
    public void waitForTabToLoad() {
        //TODO add code
    }    
}
