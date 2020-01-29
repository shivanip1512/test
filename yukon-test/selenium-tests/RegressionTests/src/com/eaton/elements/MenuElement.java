package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MenuElement {

    private WebDriver driver;

    public MenuElement(WebDriver driver) {
        this.driver = driver;
    }
    
    private WebElement getMenu() {
        return this.driver.findElement(By.cssSelector(".menus"));
    }

    private List<WebElement> getAllMenuItems() {

        List<WebElement> menuItems = getMenu().findElements(By.cssSelector(".menu.dropdown"));
        
        return menuItems;
    }

    private WebElement findParent(String menuTitle) {
        List<WebElement> elementList = getAllMenuItems();
        
        WebElement parentElement = null;
        for (WebElement element : elementList) {

            WebElement menuItem = element.findElement(By.cssSelector(".menu-title"));

            String itemText = menuItem.getText();
            if (itemText.equals(menuTitle)) {                
               parentElement = element;
               return parentElement;
            } 
        }
        
        return parentElement;
    }
    
    private WebElement getMenuItem(String menuTitle) {
        
        List<WebElement> elementList = getAllMenuItems();
        
        WebElement menuItem = null;
        for (WebElement element : elementList) {

            WebElement item = element.findElement(By.cssSelector(".menu-title"));

            String itemText = item.getText();
            if (itemText.equals(menuTitle)) {                
                menuItem = item;

                return menuItem;
            } 
        }
        
        return menuItem;
    }

    private List<WebElement> getMenuOptions(String menuTitle) {
        
        WebElement item = findParent(menuTitle);

        return item.findElements(By.cssSelector(".menu-options .menu-option"));        
    }
    
    public void clickMenuTitle(String menuTitle) {

        WebElement item = getMenuItem(menuTitle);
        item.click();
    }
    
    public String getMenuOptionUrl(String menuTitle, String optionTitle) {
        
        List<WebElement> options = getMenuOptions(menuTitle);                
        
        String url = null;
        for (WebElement option : options) {
            WebElement element = option.findElement(By.cssSelector(".menu-option-link"));
            String optionText = element.getAttribute("innerText");
            if (optionText.equals(optionTitle)) {
                url = element.getAttribute("href");
                return url;
            }
        }
        
        return url;        
    }
}
