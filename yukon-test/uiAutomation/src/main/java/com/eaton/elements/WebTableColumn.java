package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class WebTableColumn{

    private WebElement column;
	private DriverExtensions driverExt;

    public WebTableColumn(DriverExtensions driverExt) {
    	this.driverExt = driverExt;
    }
   
    public List<WebElement> getColumnValues(int cellIndex) {

        return this.driverExt.findElements(By.cssSelector("tr td:nth-child(" + cellIndex + ")"), Optional.empty());
        		
    } 
 
    
    public List<WebElement> getLinkValues(int cellIndex) {

        return this.driverExt.findElements(By.cssSelector("tr td:nth-child(" + cellIndex + ") a"), Optional.empty());
        		
    } 
}
