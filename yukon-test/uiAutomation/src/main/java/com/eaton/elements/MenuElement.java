package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class MenuElement {

    private DriverExtensions driverExt;

    public MenuElement(DriverExtensions driverExt) {
        this.driverExt = driverExt;
    }

    private WebElement getMenu() {
        return this.driverExt.findElement(By.cssSelector(".menus"), Optional.of(3));
    }

    private List<WebElement> getAllMenuItems() {

        return getMenu().findElements(By.cssSelector(".menu.dropdown"));
    }

    private WebElement getParent(int parentIndex) {
        List<WebElement> elementList = getAllMenuItems();

        return elementList.get(parentIndex);
    }

    public void clickMenuTitle(int parentIndex) {
        WebElement item = getParent(parentIndex);

        item.click();
    }

    public String getMenuOptionUrl(int parentIndex, int childIndex) {
        WebElement parent = getParent(parentIndex);

        List<WebElement> children = parent.findElements(By.cssSelector(".menu-options .menu-option"));

        WebElement child = children.get(childIndex);

        WebElement option = child.findElement(By.cssSelector(".menu-option-link"));

        return option.getAttribute("href");
    }
}
