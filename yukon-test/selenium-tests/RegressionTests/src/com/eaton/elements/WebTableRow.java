package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebTableRow{

    private WebElement row;

    public WebTableRow(WebElement row) {
        this.row = row;
    }

    public WebElement getCell(int cellIndex) {

        return this.row.findElement(By.cssSelector("tr:nth-child(" + cellIndex + ")"));
    } 
    
    public void selectCellByLink() {
        this.row.findElement(By.cssSelector("a")).click();
    }
}
