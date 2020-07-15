package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Section {

    private DriverExtensions driverExt;
    private String sectionName;
    private WebElement section;

    public Section(DriverExtensions driverExt, String sectionName) {
        this.driverExt = driverExt;
        this.sectionName = sectionName;
        
        setSection();
    }
    public Section(DriverExtensions driverExt, String sectionName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.sectionName = sectionName;
        
        setSection();
    }
    
    private void setSection() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".section-container"), Optional.empty());
        
        section =  list.stream().filter(element -> element.findElement(By.cssSelector(".title-bar .title")).getText().contains(this.sectionName)).findFirst().orElseThrow();  
    }

    
    public WebElement getSection() {        
        return section;
    }    
}
