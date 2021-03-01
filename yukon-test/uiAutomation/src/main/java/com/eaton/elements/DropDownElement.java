package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.eaton.framework.DriverExtensions;

public class DropDownElement {
    
    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;

    public DropDownElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
    }
    
    public DropDownElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public DropDownElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }
    
    public void selectItemByValue(String text)
    {
        if (text.isBlank())
        {
            return;
        }

        Select dropDown = new Select(getSelectElement());
        
        dropDown.selectByValue(text);
    }    
    
    public void selectItemByIndex(int index)
    {
        new Select(getSelectElement()).selectByIndex(index);
    }
    
    public int getOptionCount() {
        List<WebElement> options = getSelectElement().findElements(By.tagName("option"));

        return options.size();
    }
    
    public List<String> getOptionValues() {
        List<String> optionValues = new ArrayList<>();
        List<WebElement> options = getSelectElement().findElements(By.tagName("option"));
        for(WebElement option : options) {
            optionValues.add(option.getText().trim().replace("\n", ""));
        }
        
        return optionValues;
    }
    
    private WebElement getSelectElement() {
        if (this.parentName != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] select[name='" + this.elementName + "']"), Optional.of(3));
        } else if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("select[name='" + this.elementName + "']"));
        } else {
            return this.driverExt.findElement(By.cssSelector("select[name='" + this.elementName + "']"), Optional.of(3));   
        }        
    }  
    
    public String getValidationError() {
        return this.driverExt.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.of(3)).getText();
     }
    
    public String getSelectedValue() {
    	List<WebElement> elements = getSelectElement().findElements(By.tagName("option"));
    	WebElement element = elements.stream().filter(x -> x.isSelected()).findFirst().orElseThrow();
    	return element.getText();
    }
    
    public String getOptionValue() {
        List<WebElement> elements = getSelectElement().findElements(By.tagName("option"));
        WebElement element = elements.stream().filter(x -> x.isSelected()).findFirst().orElseThrow();
        return element.getAttribute("value");
    }
}


