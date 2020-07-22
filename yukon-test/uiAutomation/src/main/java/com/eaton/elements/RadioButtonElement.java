package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class RadioButtonElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
    }
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName, String parentName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
    }
    
    public void setByValue(String value, boolean check) {
        List<WebElement> elements = getRadioButtons();
        
        for (WebElement element : elements) {
            String v = element.getAttribute("value");            
                                   
            if (v.equals(value)) {
                
                String checked = element.getAttribute("checked");
                
                if(!checked.equals("checked") && check) {
                    element.click();   
                }                                
                
                return;
            }
        }
    }     
    
    public List<String> getValues() {
        List<WebElement> elements = getRadioButtons();
        
        List<String> values = new ArrayList<>();
        for (WebElement element : elements) {
            String value = element.getAttribute("value");
            
            values.add(value);
        }
        
        return values;
    }
    
    private List<WebElement> getRadioButtons() {
        if(this.parentName != null) {
            return this.driverExt.findElements(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            return this.parentElement.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            return this.driverExt.findElements(By.cssSelector("input[name = '" + this.elementName + "']"), Optional.empty());   
        }        
    }
    
    public String getValueChecked() {
    	List<WebElement> elements = getRadioButtons(); 	
    	WebElement element = elements.stream().filter(x -> x.isSelected()).findFirst().orElseThrow();   	
    	return element.getAttribute("value");
    }
    
    public void scrollTo() {
       List<WebElement> list = getRadioButtons();
       WebElement el = list.get(0);
       
       SeleniumTestSetup.scrollToElement(el);
    }
    
    public void moveTo() {
        List<WebElement> list = getRadioButtons();
        WebElement el = list.get(0);
        
        SeleniumTestSetup.moveToElement(el);
    }
}
