package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebRow {

    private WebElement row;

    public WebRow(WebElement row) {
        this.row = row;
    }

    public void selectRow() {
        this.row.click();
    }

    public String getCellText(int cellIndex) {

        return this.row.findElement(By.cssSelector("" + cellIndex)).getText();
    }
}
