package com.eaton.elements;

import org.openqa.selenium.WebElement;

public class ColumnHeader {
	
	private WebElement webColumnHeader;
	
	public ColumnHeader(WebElement webColumnHeader) {
		webColumnHeader = webColumnHeader;
	}

	public String getColumnName() {
		return webColumnHeader.getText();
	}
}
