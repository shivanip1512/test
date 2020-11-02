package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class CreateBtnDropDownElement {

    private DriverExtensions driverExt;
    private WebElement parentElement;

    public CreateBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public CreateBtnDropDownElement(DriverExtensions driverExt, WebElement parentElement) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;
    }

    public WebElement getCreateBtn() {
        if (parentElement != null) {
            return this.parentElement.findElement(By.cssSelector(".dropdown-trigger"));
        } else {
            List<WebElement> buttons = this.driverExt.findElements(By.cssSelector(".dropdown-trigger"), Optional.of(3));

            return buttons.stream().filter(b -> b.findElement(By.cssSelector("button .b-label")).getText().contains("Create")).findFirst().orElseThrow();
        }
    }

    public void clickAndWait() {
        getCreateBtn().findElement(By.cssSelector("button")).click();
        
        String attr = "";
        long startTime = System.currentTimeMillis();

        while ((!attr.contains("menu-open")) && ((System.currentTimeMillis() - startTime) < 2000)) {
            attr = getCreateBtn().getAttribute("class");
        }
    }

    public Boolean isDisplayed() {
        return getCreateBtn().findElement(By.cssSelector("button")).isDisplayed();
    }

    public Boolean isEnabled() {
        return getCreateBtn().findElement(By.cssSelector("button")).isEnabled();
    }

    public void clickAndSelectOptionByText(String value) {
        clickAndWait();

        WebElement el = SeleniumTestSetup.getDriverExt().getDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dropdown-menu[style*='display: block;']")));

        List<WebElement> options = el.findElements(By.cssSelector(".dropdown-option"));

        WebElement optionValue = options.stream().filter(option -> option.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(value)).findFirst().orElseThrow();

        optionValue.click();
    }

    /**
     * This method is used to get the link/href attribute from Action Dropdown
     * 
     * @param text - The text listed in dropdown
     * @return - returns href attribute of option in dropdown
     */
    public String getOptionLinkByText(String text) {
        clickAndWait();

        WebElement el = SeleniumTestSetup.getDriverExt().getDriverWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dropdown-menu[style*='display: block;']")));

        List<WebElement> options = el.findElements(By.cssSelector(".dropdown-option"));

        WebElement element = options.stream().filter(x -> x.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(text)).findFirst().orElseThrow();
        
        return element.findElement(By.cssSelector("a")).getAttribute("href");
    }
}
