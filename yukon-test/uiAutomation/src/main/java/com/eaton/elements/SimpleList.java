package com.eaton.elements;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SimpleList {

    private DriverExtensions driverExt;
    private String listClassName;
    private List<WebElement> simpleListItems = null;
    private WebElement parentElement;

    public SimpleList(DriverExtensions driverExt, String listClassName) {
        this.driverExt = driverExt;
        this.listClassName = listClassName;
    }    
    
    public SimpleList(DriverExtensions driverExt, String listClassName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.listClassName = listClassName;
        this.parentElement = parentElement;        
    }
    
    private WebElement getSimpleList() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("." + this.listClassName));
        } else {
            return this.driverExt.findElement(By.cssSelector("." + this.listClassName), Optional.empty()); 
        }
    }
    
    public List<WebElement> getSimpleListItems() {

        if (this.simpleListItems == null) {
            findSimpleListItems();
        }

        return this.simpleListItems;
    }
    
    private void findSimpleListItems() {

        this.simpleListItems = this.getSimpleList().findElements(By.cssSelector("li"));
    }
    
    public WebElement getSimpleListItemAt(int index) {

        if (this.simpleListItems == null) {
            findSimpleListItems();
        }

        return this.simpleListItems.get(index);
    }
    
    public WebElement getSimpleListItemLinkAt(int index) {
        
        WebElement listItem = getSimpleListItemAt(index);
        WebElement listItemLink = listItem.findElement(By.cssSelector("*"));
        return listItemLink;
    }
    
    public String getSimpleListItemAnchorTextAt(int index) {

        return getSimpleListItemAt(index).getText();
    }
    
    public String getSimpleListItemLinkTextAt(int index) {
        
        return getLinkFromOuterHTML(getSimpleListItemLinkAt(index).getAttribute("outerHTML"));
    }
    
    public boolean getSimpleListItemLinkEnabledAt(int index) {
        
        boolean enabled = true;
        
        String linkClassText = getSimpleListItemLinkAt(index).getAttribute("class");
        if(linkClassText.equalsIgnoreCase("disabled")) {
                enabled = false;
        }
        return  enabled;
    }
    
    private String getLinkFromOuterHTML(String html)
    {
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher m = p.matcher(html);
        String link = null;
        if (m.find()) {
            link = m.group(1); // this variable should contain the link URL
        }
        return link;
    }
    
}
