package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditElement {

    private WebDriver driver;
    private WebElement input;
    private String elementName;

    public EditElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
        this.input = getEditElement();
    }

    public void clearInputValue() {
        this.input.clear();
    }

    public void setInputValue(String value) {
        this.input.sendKeys(value);
    }

    private WebElement getEditElement() {
        return this.driver.findElement(By.cssSelector("input[name='" + this.elementName + "']"));
    }
}
