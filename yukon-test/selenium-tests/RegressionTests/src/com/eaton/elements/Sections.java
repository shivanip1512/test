package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Sections {

    private DriverExtensions driverExt;
    private String parentId;

    public Sections(DriverExtensions driverExt, String parentId) {
        this.driverExt = driverExt;
        this.parentId = parentId;
    }

    public WebElement getSectionByName(String sectionName) {
        
        List<WebElement> list = this.driverExt.findElements(By.cssSelector("#" + this.parentId + " .section-container"), Optional.empty());
        
        return list.stream().filter(element -> element.findElement(By.cssSelector(".title-bar .title")).getText().contains(sectionName)).findFirst().orElseThrow();
                
//        for (WebElement element : list) {
//            String name = element.findElement(By.cssSelector(".title-bar .title")).getText();
//            
//            if (name.equals(sectionName)) {
//                return element;
//            }
//        }        
    }    
}
