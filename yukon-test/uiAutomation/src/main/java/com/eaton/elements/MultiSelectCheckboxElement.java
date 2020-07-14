package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class MultiSelectCheckboxElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private List<WebElement> checkbox;

    public MultiSelectCheckboxElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;

        setCheckbox();
    }

    public MultiSelectCheckboxElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;

        setCheckbox();
    }

    public MultiSelectCheckboxElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;

        setCheckbox();
    }

    public List<String> isDisabled() {
        List<WebElement> checkboxList = getCheckbox();
        List<String> checked= new ArrayList<String>();
        for (WebElement element : checkboxList) {
            if (element.getAttribute("disabled")==null) {
                checked.add("false");
            } else {
            checked.add("true");
            }
        }
        return checked;
    }

    private void setCheckbox() {
        if (this.parentName != null) {
            this.checkbox = this.driverExt.findElements(
                    By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"),
                    Optional.empty());
        } else if (this.parentElement != null) {
            this.checkbox = this.parentElement.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            this.checkbox = this.driverExt.findElements(By.cssSelector("input[name = '" + this.elementName + "']"),
                    Optional.empty());
        }
    }

    private List<WebElement> getCheckbox() {
        return checkbox;
    }
}
