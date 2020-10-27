package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SwitchBtnYesNoElement {

    private String elementName;
    private Boolean inverted;
    private WebElement parentElement;
    private DriverExtensions driverExt;

    public SwitchBtnYesNoElement(DriverExtensions driverExt, String elementName, WebElement parentElement) {
    	this(driverExt, elementName, parentElement, false);
    }
    
    public SwitchBtnYesNoElement(DriverExtensions driverExt, String elementName, WebElement parentElement, Boolean inverted) {
        this.driverExt = driverExt;
        this.elementName = elementName;
        this.parentElement = parentElement;
        this.inverted = inverted;
    }
    
    public SwitchBtnYesNoElement(DriverExtensions driverExt, String elementName) {
    	this(driverExt, elementName, null, false);
    }
    
	public void selectValue(String value) {

		if (!(getCheckedValue().contentEquals(value))) {
			List<WebElement> list = getSwitchBtn().findElements(By.cssSelector(".b-label"));

			WebElement el = list.stream().filter(x -> x.getText().contains(value)).findFirst().orElseThrow();

			el.click();
		}
	}
    
    public WebElement getSwitchBtn() {                
        if (parentElement != null) {
            List<WebElement> list = parentElement.findElements(By.cssSelector(".switch-btn"));

            for (WebElement webElement : list) {
                List<WebElement> element = webElement.findElements(By.cssSelector("input[name='" + this.elementName + "']"));

                if (!element.isEmpty()) {
                    return webElement;
                }
            }
        } else {
            List<WebElement> list = this.driverExt.findElements(By.cssSelector(".switch-btn"), Optional.of(3));

            for (WebElement webElement : list) {
                List<WebElement> element = webElement.findElements(By.cssSelector("input[name='" + this.elementName + "']"));

                if (!element.isEmpty()) {
                    return webElement;
                }
            }
        }


        return null;
    }    

    public String getCheckedValue() {        
        if(parentElement != null) {
            WebElement btn = parentElement.findElement(By.cssSelector("input[name='" + this.elementName + "']"));

            String id = btn.getAttribute("id");
            String isChecked = btn.getAttribute("checked");
            
            if(inverted) {
            	 if(isChecked == null) {
            		 return parentElement.findElement(By.cssSelector("label[for='" + id + "'].button.yes")).getText();
            		 
                 } else {
                	 return parentElement.findElement(By.cssSelector("label[for='" + id + "'].button.no")).getText();
                 }
            }
            else {            
	            if(isChecked == null) {
	                return parentElement.findElement(By.cssSelector("label[for='" + id + "'].button.no")).getText();
	            } else {
	                return parentElement.findElement(By.cssSelector("label[for='" + id + "'].button.yes")).getText();
	            }
            }
        } else {
            WebElement btn = this.driverExt.findElement(By.cssSelector("input[name='" + this.elementName + "']"), Optional.of(3));

            String id = btn.getAttribute("id");
            String isChecked = btn.getAttribute("checked");
            
            if(inverted) {
            	if(isChecked == null) {
            		return this.driverExt.findElement(By.cssSelector("label[for='" + id + "'].button.yes"), Optional.empty()).getText();
	            } else {
	            	return this.driverExt.findElement(By.cssSelector("label[for='" + id + "'].button.no"), Optional.empty()).getText();
	            }
            } else {
	            if(isChecked == null) {
	                return this.driverExt.findElement(By.cssSelector("label[for='" + id + "'].button.no"), Optional.empty()).getText();
	            } else {
	                return this.driverExt.findElement(By.cssSelector("label[for='" + id + "'].button.yes"), Optional.empty()).getText();
	            }
            }
        }
    }

    public Boolean getInverted() {
    	return inverted;
    }
    
    public void setInverted(Boolean inverted) {
    	this.inverted = inverted;
    }
}
