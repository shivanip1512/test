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
    private WebElement selectElement;

    public DropDownElement(DriverExtensions driverExt, String elementName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        
        setSelectElement();
    }
    
    public DropDownElement(DriverExtensions driverExt, String elementName, String parentName) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setSelectElement();
    }
    
    public DropDownElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setSelectElement();
    }
    
    public void selectItemByText(String text)
    {
        if (text.isBlank())
        {
            return;
        }

        Select dropDown = new Select(getSelectElement());
        
        dropDown.selectByVisibleText(text);
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
            optionValues.add(option.getText());
        }
        
        return optionValues;
    }
    
    private void setSelectElement() {
        if (this.parentName != null) {
            this.selectElement = this.driverExt.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] select[name='" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            this.selectElement = this.parentElement.findElement(By.cssSelector("select[name='" + this.elementName + "']"));
        } else {
            this.selectElement = this.driverExt.findElement(By.cssSelector("select[name='" + this.elementName + "']"), Optional.empty());   
        }        
    }  
    
    public WebElement getSelectElement() {
        return selectElement;
    }
    
    public String getValidationError() {
        return this.driverExt.findElement(By.cssSelector("span[id='" + this.elementName + ".errors']"), Optional.empty()).getText();
     }
    
    public String getSelectedValue() {
    	List<WebElement> elements = getSelectElement().findElements(By.tagName("option"));
    	WebElement element = elements.stream().filter(x -> x.isSelected()).findFirst().orElseThrow();
    	return element.getText();
    }
}


