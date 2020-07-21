package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class RadioButtonElement {

    private DriverExtensions driverExt;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private List<WebElement> radioBtns;
    
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        
        setRadioBoxes();
    }
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName, String parentName) {        
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setRadioBoxes();
    }
    
    public RadioButtonElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {        
        this.driverExt = driverExt;
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
        
        List<String> values = new ArrayList<>();
        for (WebElement element : elements) {
            String value = element.getAttribute("value");
            
            values.add(value);
        }
        
        return values;
    }
    
    private void setRadioBoxes() {
        if(this.parentName != null) {
            this.radioBtns = this.driverExt.findElements(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"), Optional.empty());
        } else if (this.parentElement != null) {
            this.radioBtns = this.parentElement.findElements(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            this.radioBtns = this.driverExt.findElements(By.cssSelector("input[name = '" + this.elementName + "']"), Optional.empty());   
        }        
    }
    
    private List<WebElement> getRadioBoxes() {
        return this.radioBtns;
    }
    
    public String getValueChecked() {
    	List<WebElement> elements = getRadioBoxes();
    	String checkedValue = "";
    	for(int i=0;i<elements.size();i++)
    	{
    		
    		boolean bValue = elements.get(i).isSelected();
        	if(bValue==true) {
        		checkedValue = elements.get(i).getAttribute("value");
        		break;
        	}
    	}
    	return checkedValue;
    }
}
