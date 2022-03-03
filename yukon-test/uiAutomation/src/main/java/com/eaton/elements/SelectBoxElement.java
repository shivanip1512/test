package com.eaton.elements;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class SelectBoxElement {

    private DriverExtensions driverExt;
    private WebElement parentElement;
    private String parentName;
    private static String available = "Available";

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
            return this.driverExt.findElement(By.cssSelector(parentName + " .select-box"), Optional.of(3));
        }
    }

    public WebElement getColumnByColumnName(String columnName) {
        List<WebElement> columns = getSelectBox().findElements(By.cssSelector(".column"));

        return columns.stream().filter(element -> element.getText().contains(columnName)).findFirst().orElseThrow();
    }

    public void selectAllAvailable() {

        this.driverExt.findElement(By.id("picker-js-avaliable-groups-picker-select-all"), Optional.of(3)).click();

        clickAdd();
    }

    public void addSingleAvailable(String value) {
        WebTable availableTable = getAvailabeTable();

        WebElement column = getColumnByColumnName(available);
        availableTable.searchTable(value, column);
        
        availableTable = getAvailabeTable();
        WebTableRow row = availableTable.getDataRowByLinkName(value);

        row.selectCellByLink();

        clickAdd();
    }

    public void addMultipleAvailable(List<String> values) {
        for (String value : values) {
            WebTable availableTable = getAvailabeTable();

            WebElement column = getColumnByColumnName(available);
            availableTable.searchTable(value, column);
            
            WebTableRow row = availableTable.getDataRowByLinkName(value);

            row.selectCellByLink();

            availableTable.clearSearch();
        }

        clickAdd();
    }

    private WebTable getAvailabeTable() {
        WebElement column = getColumnByColumnName(available);

        return new WebTable(this.driverExt, "compact-results-table", column);
    }

    private void clickAdd() {
        getColumnByColumnName(available).findElement(By.cssSelector("[aria-label='Add']")).click();
    }
    
    public String getColumnHeading(String columnName) {
		WebElement element = getColumnByColumnName(columnName);

		return element.findElement(By.cssSelector("h3")).getText();
	}
	
	public String getColumnSearchLabel(String columnName) {
		WebElement element = getColumnByColumnName(columnName);

		return element.findElement(By.cssSelector("div label")).getText();
	} 
	
	public String getColumnSelectAllLabel(String columnName) {
		WebElement element = getColumnByColumnName(columnName);

		return element.findElement(By.cssSelector("div .action-area label")).getText();
	}
	
	public List<String> getColumnTableHeaders(String columnName) {
		WebElement selectBoxColumn = getColumnByColumnName(columnName);

		List<WebElement> elements = selectBoxColumn.findElements(By.cssSelector("div .compact-results-table th"));

        List<String> headers = new ArrayList<>();
        for (WebElement element : elements) {
        	headers.add(element.getText());
        }

        return headers;
	}
	
    public List<String> getSectionLabelsNotification() {

        List<WebElement> nameElements = this.driverExt.findElements(By.cssSelector("div .compact-results-table.no-stripes tr td"), Optional.empty());
        List<String> names = new ArrayList<>();    

        for (WebElement element : nameElements) {
            names.add(element.getText());
        }
        return names;
    }  
	
	public String searchByValidName(String value) {
		WebTable availableTable = getAvailabeTable();

		WebElement column = getColumnByColumnName(available);
		availableTable.searchTable(value, column);

		availableTable = getAvailabeTable();
		WebTableRow row = availableTable.getDataRowByLinkName(value);

		return row.getCellLinkTextByIndex(1);
	}
	
	public String searchByInvalidName(String value) {
		TextEditElement search = new TextEditElement(this.driverExt, "ss");

		search.setInputValue(value);
		WebElement element  = this.driverExt.findElement(By.cssSelector(".compact-results-table.picker-results-table"), Optional.empty());
		this.driverExt.waitUntilElementInvisible(element);
		return this.driverExt.findElement(By.cssSelector("#picker-js-avaliable-groups-picker-no-results"),Optional.of(5)).getText();
		
	}
}
