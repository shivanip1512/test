package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SwitchBtnYesNoElement {

    private String elementName;
    private WebElement parentElement;

    public SwitchBtnYesNoElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public void setValue(boolean checked) {
        WebElement switchBtn = getSwitchBtn();

        WebElement btn = switchBtn.findElement(By.cssSelector("input[name='" + this.elementName + "']"));

        String isChecked = btn.getAttribute("checked");

        if (isChecked == null && checked) {
            switchBtn.findElement(By.cssSelector(".button.yes")).click();
        } else if (isChecked != null && !checked) {
            switchBtn.findElement(By.cssSelector(".button.no")).click();
        }
    }

    public WebElement getSwitchBtn() {
        List<WebElement> list = parentElement.findElements(By.cssSelector(".switch-btn"));

        for (WebElement webElement : list) {
            List<WebElement> element = webElement.findElements(By.cssSelector("input[name='" + this.elementName + "']"));

            if (!element.isEmpty()) {
                return webElement;
            }
        }

        return null;
    }

    public String getCheckedValue() {
        WebElement switchBtn = getSwitchBtn();

        WebElement btn = switchBtn.findElement(By.cssSelector("input[name='" + this.elementName + "']"));

        String isChecked = btn.getAttribute("checked");
        
        if(isChecked == null) {
            return switchBtn.findElement(By.cssSelector(".button.no .b-label")).getText();
        } else {
            return switchBtn.findElement(By.cssSelector(".button.yes .b-label")).getText();
        }
    }
}
