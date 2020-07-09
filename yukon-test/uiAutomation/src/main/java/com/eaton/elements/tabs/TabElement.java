package com.eaton.elements.tabs;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class TabElement {

    private DriverExtensions driverExt;
	private String elementName;
	private WebElement parentElement;

    public TabElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;        
    }    
    
    public TabElement(DriverExtensions driverExt, WebElement parentElement) {
        this.driverExt = driverExt;
        //this.elementName = elementName;
        this.parentElement = parentElement;
    }
    
    private WebElement getTabContainer() {
        return this.driverExt.findElement(By.cssSelector(".tabbed-container"), Optional.empty());
    }
    
    //Ruchita added
    private List<WebElement> getTabsContainer() {
        return this.driverExt.findElements(By.cssSelector(".tabbed-container"), Optional.empty());
    }
  //Ruchita added
    
    public WebElement getTabPanelByAriaLabels() {
        List<WebElement> tabContainer = getTabsContainer();
        WebElement x= null;
        for(WebElement element : tabContainer) {
         element.findElement(By.cssSelector(".ui-tabs-tab"));
         x=element;
        }
        return x;
     }
    //Ruchita added
    
    private List<WebElement> getTabsEdit() {
        return getTabPanelByAriaLabels().findElements(By.cssSelector(".ui-tabs-tab"));
    }
    //Ruchita added
    public List<String> getTitlesEdit() {
        List<WebElement> elements = getTabsEdit();
        
        List<String> titles = new ArrayList<String>();
        for (WebElement element : elements) {
            titles.add(element.findElement(By.cssSelector("a")).getText());
        }
        
        return titles;
    }
    
    private List<WebElement> getTabs() {
        return getTabContainer().findElements(By.cssSelector(".ui-tabs-tab"));
    }
    
    public List<String> getTitles() {
        List<WebElement> elements = getTabs();
        
        List<String> titles = new ArrayList<String>();
        for (WebElement element : elements) {
            titles.add(element.findElement(By.cssSelector("a")).getText());
        }
        
        return titles;
    }
    
    public WebElement getTabPanelByName(String tabName) {
        WebElement tab = getTabByName(tabName);
        
        String attribute = tab.getAttribute("aria-labelledby");
        
        return getTabPanelByAriaLabel(attribute);
    }
    
    public WebElement getTabPanelByAriaLabel(String label) {
       WebElement tabContainer = getTabContainer();
       
       return tabContainer.findElement(By.cssSelector(".ui-tabs-panel[aria-labelledby='" + label + "']"));
    }
    
    public void clickTab(String tabName) {
        getTabByName(tabName).click();
    }
    
    public List<String> getTabLabels(String tabName) {
        WebElement tab = getTabByName(tabName);
        
        String attribute = tab.getAttribute("aria-labelledby");
        
        WebElement panel = getTabPanelByAriaLabel(attribute);
        
        List<WebElement> nameElements = panel.findElements(By.cssSelector("table tr .name"));
        
        List<String> names = new ArrayList<String>();
        for (WebElement element : nameElements) {
            names.add(element.getText());
        }
        
        return names;
    }
    
    public List<String> getTabValues(String tabName) {
        WebElement tab = getTabByName(tabName);
        
        String attribute = tab.getAttribute("aria-labelledby");
        
        WebElement panel = getTabPanelByAriaLabel(attribute);
        
        List<WebElement> valueElements = panel.findElements(By.cssSelector("table tr .value"));
        
        List<String> values = new ArrayList<String>();
        for (WebElement element : valueElements) {
            values.add(element.getText());
        }
        
        return values;
    }
    
    private WebElement getTabByName(String tabName) {
        List<WebElement> list = getTabss();
        
        return list.stream().filter(e -> e.findElement(By.cssSelector("a")).getText().contains(tabName)).findFirst().orElseThrow();        
    }
}
