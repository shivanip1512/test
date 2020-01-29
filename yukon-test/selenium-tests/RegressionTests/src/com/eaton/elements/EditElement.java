package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditElement {

    private WebDriver driver;
    private String elementName;

    public EditElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
    }   

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driver.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"));

        return !list.isEmpty() ? true : false;
    }

    protected WebElement getEditElement() {
        return this.driver.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
    }
}
