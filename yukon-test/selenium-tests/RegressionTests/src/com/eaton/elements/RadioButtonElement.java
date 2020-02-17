package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RadioButtonElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private List<WebElement> radioBtns;
    
    
    public RadioButtonElement(WebDriver driver, String elementName) {        
        this.driver = driver;
        this.elementName = elementName;
        
        setRadioBoxes();
    }
    
    public RadioButtonElement(WebDriver driver, String elementName, String parentName) {        
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setRadioBoxes();
    }
    
    public RadioButtonElement(WebDriver driver, String elementName, WebElement parentElement) {        
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setRadioBoxes();
    }
    
    public void setByValue(String value, boolean check) {
        List<WebElement> elements = getRadioBoxes();
        
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
        List<WebElement> elements = getRadioBoxes();
        
        List<String> values = new ArrayList<String>();
        for (WebElement element : elements) {
            String value = element.getAttribute("value");
            
            values.add(value);
        }
        
        return values;
    }
    
    private void setRadioBoxes() {
        if(this.parentName != null) {
            this.radioBtns = this.driver.findElements(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            this.radioBtns = this.parentElement.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            this.radioBtns = this.driver.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));   
        }        
    }
    
    private List<WebElement> getRadioBoxes() {
        return this.radioBtns;
    }
}
