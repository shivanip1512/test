package com.eaton.elements.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Panels {

    private DriverExtensions driverExt;

    public Panels(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public List<String> getListOfPanelNames() {
        List<String> panelNames = new ArrayList<>();
        List<WebElement> panels = getPanels();
        
        for (WebElement panel : panels) {
            panelNames.add(panel.findElement(By.cssSelector(".title")).getText());
        }
        
        return panelNames;
    }

    public Integer getPanelCount() {
        return getPanels().size();
    }
    
    private List<WebElement> getPanels() {
        return this.driverExt.findElements(By.cssSelector(".titled-container.box-container"), Optional.empty());
    }
}