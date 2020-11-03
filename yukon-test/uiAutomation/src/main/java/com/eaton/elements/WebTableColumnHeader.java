package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebTableColumnHeader {
    
    private WebElement webColumnHeader;
    
    public WebTableColumnHeader(WebElement webColumnHeader) {
            this.webColumnHeader = webColumnHeader;
    }

    public String getColumnName() {
            return this.webColumnHeader.getText();
    }
    
    
    /**
     * To be used for clicking on column header
     * 
     * @param index cell index on which to be clicked
     */
    public void selectColumnNameByLink(int index) {
    	List<WebElement> headers = this.webColumnHeader.findElements(By.cssSelector("th"));
        headers.get(index).findElement(By.cssSelector("a")).click();  
    }
}
