package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebRow {

    private WebElement row;

    public WebRow(WebElement row) {
        row = row;
    }

    public void selectRow() {
        row.click();
    }

    public String getCellText(int cellIndex) {

        return row.findElement(By.cssSelector("" + cellIndex)).getText();
    }
}
