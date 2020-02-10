package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RadioButtonElement {

    WebDriver driver;
    String elementName;
    String parentName;
    
    public RadioButtonElement(WebDriver driver, String elementName, String parentName) {
        
        this.driver = driver;
        this.elementName = elementName;
    }
    
    public void setByValue(String value, Boolean check) {
        List<WebElement> elements = getRadioBoxes();
        
        for (WebElement element : elements) {
            String v = element.getAttribute("value");            
                                   
            if (v.equals(value)) {
                
                String checked = element.getAttribute("checked");
                
                if(!checked.equals(check)) {
                    element.click();   
                }                                
                
                return;
            }
        }
    }     
    
    public List<String> getValues() {
        List<WebElement> elements = getRadioBoxes();
        
        List<String> values = new ArrayList<String>();
        for (WebElement element : elements) {
            String value = element.getAttribute("value");
            
            values.add(value);
        }
        
        return values;
    }
    
    private List<WebElement> getRadioBoxes() {
        if(parentName != null) {
            return this.driver.findElements(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"));
        } else {
            return this.driver.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));   
        }        
    }
}
