package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ActionBtn {

    private WebDriver driver;
    private WebElement btn;

    public ActionBtn(WebDriver driver) {
        this.driver = driver;
        this.btn = getButton();
    }

    private WebElement getButton() {
        return this.driver.findElement(By.cssSelector(".page-actions #b-page-actions button"));
    }

//    private WebElement getDropDown() {
//        return this.driver.findElement(By.cssSelector(".page-actions .dropdown-trigger"));
//    }

//    private Boolean isOpen() {
//        WebElement dropDown = getDropDown();
//
//        return dropDown.isDisplayed();
//    }

//    private List<WebElement> dropDownOptions() {
//
//        Boolean open = isOpen();
//        if (open.equals(false))
//            click();
//
//        WebElement dropDown = getDropDown();
//
//        return dropDown.findElements(By.cssSelector(".dropdown-option .dropdown-option-label"));
//    }

    public void click() {
        this.btn.click();
    }

    public boolean isDisplayed() {
        return this.btn.isDisplayed();
    }

    public boolean isEnabled() {
        return this.btn.isEnabled();
    }

//    public void clickAndSelectOptionByText(String option) {
//
//    }
}
