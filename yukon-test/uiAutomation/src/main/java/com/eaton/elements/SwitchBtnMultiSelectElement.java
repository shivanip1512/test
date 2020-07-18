package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SwitchBtnMultiSelectElement {

    private DriverExtensions driverExt;
    private String elementName;
    private WebElement parentElement;

    public SwitchBtnMultiSelectElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    public SwitchBtnMultiSelectElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }

    public void setTrueFalseByValue(String value, boolean checked) {
        WebElement element = getSwitchBtn();

        List<WebElement> switchBtnList = element.findElements(By.cssSelector(".switch-btn"));

        WebElement switchBtn = element.findElement(By.cssSelector("input[id='" + value.toUpperCase() + "_chk']"));

        String isChecked = switchBtn.getAttribute("checked");

        for (WebElement webElement : switchBtnList) {
            List<WebElement> list = webElement.findElements(By.cssSelector("input[id='" + value.toUpperCase() + "_chk']"));

            if (!list.isEmpty()) {               
                if ((isChecked == null && checked) || (isChecked != null && !checked)) {
                    webElement.findElement(By.cssSelector("span>span")).click();
                }
            } 
        }
    }

    public boolean isValueDisabled(String name) {
        WebElement element = getSwitchBtn();

        WebElement switchBtn = element.findElement(By.cssSelector("input[id='" + name.toUpperCase() + "_chk']"));

        String disabled = switchBtn.getAttribute("disabled");

        if (disabled == null) {
            return false;
        } else return true;
    }
    
    public boolean allValuesDisabled() {
        WebElement element = getSwitchBtn();
        
        List<WebElement> list = element.findElements(By.cssSelector("label .switch-btn-checkbox"));
        
        boolean allDisabled = true;
        for (WebElement webElement : list) {
            String disabled = webElement.getAttribute("disabled");
            
            if (!disabled.equals("true")) {
                 return false;                
            }
        }
        
        return allDisabled;
                
//        list.stream().filter(x -> x.findElement(By.cssSelector(".title")).getText().contains(panelName))
//                .findFirst();
    }

    private WebElement getSwitchBtn() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("." + this.elementName));
        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.elementName), Optional.empty());
        }
    }
}
