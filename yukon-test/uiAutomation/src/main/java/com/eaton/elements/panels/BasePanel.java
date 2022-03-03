package com.eaton.elements.panels;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class BasePanel {

    private DriverExtensions driverExt;
    private String panelName;

    public BasePanel(DriverExtensions driverExt, String panelName) {
        this.driverExt = driverExt;
        this.panelName = panelName;
    }

    public WebElement getPanel() {
        List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".titled-container"), Optional.empty());

        return elements.stream().filter(element -> element.findElement(By.cssSelector(".title")).getText().contains(panelName))
                .findFirst().orElseThrow();
    }
    
    public String getPanelName() {
        return panelName;
    }
}
