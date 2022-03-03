package com.eaton.elements;

import org.openqa.selenium.WebElement;

public class WebTableColumnHeader {
    
    private WebElement webColumnHeader;
    
    public WebTableColumnHeader(WebElement webColumnHeader) {
            this.webColumnHeader = webColumnHeader;
    }

    public String getColumnName() {
            return this.webColumnHeader.getText();
    }
}