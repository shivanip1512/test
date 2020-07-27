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

    public CreateBtnDropDownElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    public WebElement getCreateBtn() {
        List<WebElement> buttons = this.driverExt.findElements(By.cssSelector(".page-actions .dropdown-trigger button"),
                Optional.empty());

        return buttons.stream().filter(button -> button.findElement(By.cssSelector(".b-label")).getText().contains("Create"))
                .findFirst().orElseThrow();
    }

    public void click() {
        getCreateBtn().click();
    }

    public Boolean isDisplayed() {
        return getCreateBtn().isDisplayed();
    }

    public Boolean isEnabled() {
        return getCreateBtn().isEnabled();
    }

    public void clickAndSelectOptionByText(String value) {
        click();

        WebElement el = SeleniumTestSetup.getDriverExt().getDriverWait()
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dropdown-menu[style*='display: block;']")));

        List<WebElement> options = el.findElements(By.cssSelector(".dropdown-option"));

        WebElement optionValue = options.stream().filter(option -> option.findElement(By.cssSelector(".dropdown-option-label")).getText().equals(value)).findFirst().orElseThrow();

        optionValue.click();

//        }  //TODO add an exception stating did not find dropdown
    }
}
