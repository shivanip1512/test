package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebTableRow {

    private WebElement row;

    public WebTableRow(WebElement row) {
        this.row = row;
    }

    private WebElement getCell(int cellIndex) {

        return this.row.findElement(By.cssSelector("td:nth-child(" + cellIndex + ")"));
    }
    
    
}
