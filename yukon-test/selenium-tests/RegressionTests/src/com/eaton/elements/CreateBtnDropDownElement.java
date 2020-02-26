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

//        WebElement element = null;
//        long startTime = System.currentTimeMillis();
//        while (element == null && System.currentTimeMillis() - startTime < 3000) {
//            element = this.driverExt.findElement(By.cssSelector(".dropdown-menu[style*='display: block;']"), Optional.empty());
//        }

        WebElement el = SeleniumTestSetup.getDriverExt().getDriverWait()
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dropdown-menu[style*='display: block;']")));

        List<WebElement> options = el.findElements(By.cssSelector(".dropdown-option-label"));

        WebElement optionValue = options.stream().filter(option -> option.getText().equals(value)).findFirst().orElseThrow();

        optionValue.click();

//            for (WebElement option : options) {
//                String optionText = option.getText();
//                if (optionText.equals(value)) {
//                    option.click();
//                    return;
//                }
//            }
//        }  //TODO add an exception stating did not find dropdown
    }
}
