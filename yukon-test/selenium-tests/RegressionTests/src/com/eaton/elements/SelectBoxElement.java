package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SelectBoxElement {

    private DriverExtensions driverExt;
    private WebElement parentElement;
    private String parentName;
    private static String AVAILABLE = "Available";

    public SelectBoxElement(DriverExtensions driverExt, WebElement parentElement) {
        this.driverExt = driverExt;
        this.parentElement = parentElement;
    }

    public SelectBoxElement(DriverExtensions driverExt, String parentName) {
        this.driverExt = driverExt;
        this.parentName = parentName;
    }

    private WebElement getSelectBox() {
        if (parentElement != null) {
            return this.parentElement.findElement(By.cssSelector(".select-box"));
        } else {
            return this.driverExt.findElement(By.cssSelector(parentName + " .select-box"), Optional.empty());
        }
    }

    private WebElement getColumnByColumnName(String columnName) {
        List<WebElement> columns = getSelectBox().findElements(By.cssSelector(".column"));

        return columns.stream().filter(element -> element.getText().contains(columnName))
                .findFirst().orElseThrow();
    }

    public void selectAllAvailable() {

        this.driverExt.findElement(By.id("picker-js-avaliable-groups-picker-select-all"), Optional.empty()).click();

        clickAdd();
    }

    public void addSingleAvailable(String value) {

        WebTable availableTable = getAvailabeTable();

        WebElement column = getColumnByColumnName(AVAILABLE);
        availableTable.searchTable(value, column);
        
        availableTable = getAvailabeTable();
        WebTableRow row = availableTable.getDataRowByName(value);

        row.selectCellByLink();

        clickAdd();
    }

    public void addMultipleAvailable(List<String> values) {
        for (String value : values) {
            WebTable availableTable = getAvailabeTable();

            WebElement column = getColumnByColumnName(AVAILABLE);
            availableTable.searchTable(value, column);
            
            WebTableRow row = availableTable.getDataRowByName(value);

            row.selectCellByLink();

            availableTable.clearSearch();
        }

        clickAdd();
    }

    private WebTable getAvailabeTable() {
        WebElement column = getColumnByColumnName(AVAILABLE);

        return new WebTable(this.driverExt, "compact-results-table", column);
    }

    private void clickAdd() {
        getColumnByColumnName(AVAILABLE).findElement(By.cssSelector("[aria-label='Add']")).click();
    }
}
