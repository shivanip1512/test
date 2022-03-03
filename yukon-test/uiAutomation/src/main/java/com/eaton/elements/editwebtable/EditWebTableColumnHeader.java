package com.eaton.elements.editwebtable;

import org.openqa.selenium.WebElement;

public class EditWebTableColumnHeader {

    private WebElement webColumnHeader;

    public EditWebTableColumnHeader(WebElement webColumnHeader) {
        this.webColumnHeader = webColumnHeader;
    }

    public String getColumnName() {
        return this.webColumnHeader.getText();
    }
}
