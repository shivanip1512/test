package com.eaton.elements.panels;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasePanel {

    private WebDriver driver;
    private String panelName;

    public BasePanel(WebDriver driver, String panelName) {
        this.driver = driver;
        this.panelName = panelName;
    }

    protected WebElement getPanel() {
        List<WebElement> elements = this.driver.findElements(By.cssSelector(".titled-container"));

        return elements.stream().filter(element -> element.findElement(By.cssSelector(".title")).getText().contains(panelName))
                .findFirst().orElseThrow();
    }
}
