package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Section {

    private DriverExtensions driverExt;
    private String sectionName;
    private WebElement sectionElement;

    public Section(DriverExtensions driverExt, String sectionName) {
        this.driverExt = driverExt;
        this.sectionName = sectionName;
        
        setSection();
    }
    
    private void setSection() {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".section-container"), Optional.empty());
        
        sectionElement =  list.stream().filter(element -> element.findElement(By.cssSelector(".title-bar .title")).getText().contains(this.sectionName)).findFirst().orElseThrow();  
    }

    public WebElement getSection() {        
        return sectionElement;
    }    

    public List<String> getSectionLabels() {

        List<WebElement> nameElements = sectionElement.findElements(By.cssSelector("table tr .name"));
        List<String> names = new ArrayList<>();

        for (WebElement element : nameElements) {
            names.add(element.getText());
        }
        return names;
    }
}
