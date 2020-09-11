package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebTableRow{

    private WebElement row;

    public WebTableRow(WebElement row) {
        this.row = row;
    }

    public WebElement getCell(int cellIndex) {

        return this.row.findElement(By.cssSelector("tbody tr>td:nth-child(" + cellIndex + ")"));
    } 
    
    public void selectCellByLink() {
        this.row.findElement(By.cssSelector("a")).click();        
    }
    
    public String getCellLinkByIndex(int index) {
        List<WebElement> cells = this.row.findElements(By.cssSelector("td"));
        return cells.get(index).findElement(By.cssSelector("a")).getAttribute("href");
    }
    
    public void clickIcon(Icon icon) {
        this.row.findElement(By.cssSelector("." +icon.getIcon())).click();
    }    
    
    public enum Icon {
        COG("icon-cog"),
        PENCIL("icon-pencil"),
        REMOVE("icon-cross"),
        NOTES("icon-notes-pin");

        private final String icon;

        Icon(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return this.icon;
        }
    }
}