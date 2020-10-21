package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class WebTableColumnHeader {

    private WebElement webColumnHeader;

    public WebTableColumnHeader(WebElement webColumnHeader) {
        this.webColumnHeader = webColumnHeader;
    }

    public String getColumnName() {
        return this.webColumnHeader.getText();
    }

    public void clickHeader() {
        this.webColumnHeader.findElement(By.cssSelector("a")).click();
    }

    public String getSortedBy() {
        long startTime = System.currentTimeMillis();
        String sortable = "";
        try {
            while (sortable == "") {
                sortable = this.webColumnHeader.findElement(By.cssSelector("a")).getAttribute("class");
            }
        } catch (StaleElementReferenceException ex) {
        }

        if (sortable.toLowerCase().contains(SortDirection.ASCENDING.getSortDirection())) {
            return "SortDirection.ASCENDING.getSortDirection()";
        } else if (sortable.toLowerCase().contains(SortDirection.DESCENDING.getSortDirection())) {
            return SortDirection.DESCENDING.getSortDirection();
        } else {
            return "";
        }
    }

    public enum SortDirection {
        ASCENDING("asc"),
        DESCENDING("desc");

        private final String direction;

        SortDirection(String direction) {
            this.direction = direction;
        }

        public String getSortDirection() {
            return this.direction;
        }
    }
}
