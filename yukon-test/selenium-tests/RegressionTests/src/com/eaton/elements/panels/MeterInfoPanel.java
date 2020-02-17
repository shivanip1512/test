package com.eaton.elements.panels;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;

public class MeterInfoPanel extends BasePanel {

    private WebDriver driver;
    private WebElement panel;
    
    public MeterInfoPanel(WebDriver driver, String panelName) {
        super(driver, panelName);
        
        this.driver = driver;
        this.panel = getPanel();
    }

    public Button getEdit() {
        return new Button(this.driver, "Edit", this.panel);
    }    
}
