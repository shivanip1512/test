package com.eaton.elements;

import org.openqa.selenium.WebElement;

public class ColumnHeader {
    
    private WebElement webColumnHeader;
    
    public ColumnHeader(WebElement webColumnHeader) {
            this.webColumnHeader = webColumnHeader;
    }

    public String getColumnName() {
            return this.webColumnHeader.getText();
    }
}
