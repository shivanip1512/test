package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;

    public EditElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
    }   

    public Boolean errorDisplayed() {
        List<WebElement> list = this.driver.findElements(By.cssSelector("span[id='" + this.elementName + ".errors']"));

        return !list.isEmpty() ? true : false;
    }

    protected WebElement getEditElement() {
        if (this.parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name='" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
        }        
    }
}
