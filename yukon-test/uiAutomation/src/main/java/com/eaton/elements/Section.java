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
    private WebElement parentElement;

    public Section(DriverExtensions driverExt, String sectionName) {
        this.driverExt = driverExt;
        this.sectionName = sectionName;
    }

    public Section(DriverExtensions driverExt, String sectionName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.sectionName = sectionName;
        this.parentElement = parentElement;
    }

    public WebElement getSection() {
        if (parentElement != null) {
            List<WebElement> list = this.parentElement.findElements(By.cssSelector(".section-container"));
            return list.stream().filter(element -> element.findElement(By.cssSelector(".title-bar .title")).getText().contains(this.sectionName)).findFirst().orElseThrow();
        } else {
            List<WebElement> list = this.driverExt.findElements(By.cssSelector(".section-container"), Optional.of(3));
            return list.stream().filter(element -> element.findElement(By.cssSelector(".title-bar .title")).getText().contains(this.sectionName)).findFirst().orElseThrow();
        }
    }

    public List<String> getSectionLabels() {

        List<WebElement> nameElements = getSection().findElements(By.cssSelector("table tr .name"));
        List<String> names = new ArrayList<>();    

        for (WebElement element : nameElements) {
            names.add(element.getText());
        }
        return names;
    }    
    
    public List<String> getSectionValues() {

        List<WebElement> nameElements = getSection().findElements(By.cssSelector("table tr .value"));
        List<String> names = new ArrayList<>();    

        for (WebElement element : nameElements) {
            names.add(element.getText());
        }
        return names;
    }     
}
