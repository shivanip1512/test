package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class ActionBtnDropDownElement {

    private DriverExtensions driverExt;

    public ActionBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public WebElement getActionBtn() {
        return this.driverExt.findElement(By.cssSelector(".page-actions #b-page-actions"), Optional.of(0));
    }

    public void clickAndWait() {
        getActionBtn().findElement(By.cssSelector("button")).click();
        
        String attr = "";
        long startTime = System.currentTimeMillis();

        while ((!attr.contains("menu-open")) && ((System.currentTimeMillis() - startTime) < 2000)) {
            attr = getActionBtn().getAttribute("class");
        }
    }

    public Boolean isDisplayed() {
        return getActionBtn().findElement(By.cssSelector("button")).isDisplayed();
    }

    public Boolean isEnabled() {
        return getActionBtn().findElement(By.cssSelector("button")).isEnabled();
    }

    public void clickAndSelectOptionByText(String value) {
        clickAndWait();

        WebElement element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));

        List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option"));

        WebElement optionValue = options.stream().filter(option -> option.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(value)).findFirst().orElseThrow();
        
        optionValue.click();
    }

    /**
     * @param text - The Text listed in the dropdown
     * @return - returns true if the Action in the dropdown is enabled
     *         returns false if the Action in the dropdown is disabled
     */
    public Boolean isActionEnabled(String text) {
        WebElement element = null;
        element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));

        List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option-label"));

        WebElement el = options.stream().filter(x -> x.getText().equals(text)).findFirst().orElseThrow();

        return !el.getAttribute("class").contains("disabled");
    }

    /**
     * This method is used to get the link/href attribute from Action Dropdown
     * 
     * @param text - The text listed in dropdown
     * @return - returns href attribute of option in dropdown
     */
    public String getOptionLinkByText(String text) {
        clickAndWait();

        WebElement element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.of(3));

        List<WebElement> options = element.findElements(By.cssSelector(".dropdown-option"));

        WebElement el = options.stream().filter(x -> x.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(text)).findFirst().orElseThrow();
        
        return el.findElement(By.cssSelector("a")).getAttribute("href");
    }
}
