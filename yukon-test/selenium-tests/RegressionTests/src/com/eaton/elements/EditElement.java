package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditElement {

    private WebDriver driver;
    private WebElement input;
    private String elementName;

    public EditElement(WebDriver driver, String elementName) {
        driver = driver;
        elementName = elementName;
        input = getEditElement();
    }

    public void clearInputValue() {
        input.clear();
    }

    public void setInputValue(String value) {
        input.sendKeys(value);
    }

    private WebElement getEditElement() {
        return driver.findElement(By.cssSelector("input[name='" + elementName + "']"));
    }
}
