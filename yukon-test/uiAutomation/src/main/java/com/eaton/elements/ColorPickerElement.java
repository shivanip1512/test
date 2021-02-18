package com.eaton.elements;

import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.builders.tools.trends.TrendTypes;
import com.eaton.framework.DriverExtensions;

public class ColorPickerElement {

    private DriverExtensions driverExt;
    private String elementName;

    public ColorPickerElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }

    private WebElement getPickerElement() {

        return this.driverExt.findElement(By.cssSelector(elementName), Optional.of(3));

    }

    public void selectColorByTitle(String color) {
        getPickerElement().click();

        WebElement palette = this.driverExt.findElement(By.cssSelector(".sp-container[style*=top] .sp-palette span[title='" + color + "']"), Optional.of(3));
        palette.click();        
    }
    
    public String getSelectedColor() {
        WebElement el =  this.driverExt.findElement(By.cssSelector(".js-color-picker"), Optional.empty());
        String hexColor = el.getAttribute("value");           
        
        return TrendTypes.getcolorNameFromHex(hexColor);
    }
}
