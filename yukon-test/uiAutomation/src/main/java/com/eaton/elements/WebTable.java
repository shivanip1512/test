package com.eaton.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class WebTable {

    private DriverExtensions driverExt;
    private String tableClassName;
    private WebElement parentElement;
    private String parent;

    public WebTable(DriverExtensions driverExt, String tableClassName) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
    }

    public WebTable(DriverExtensions driverExt, String tableClassName, WebElement parentElement) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parentElement = parentElement;
    }

    public WebTable(DriverExtensions driverExt, String tableClassName, String parent) {
        this.driverExt = driverExt;
        this.tableClassName = tableClassName;
        this.parent = parent;
    }

    private WebElement getTable() {
        if (this.parentElement != null) {
            return this.parentElement.findElement(By.cssSelector("table." + this.tableClassName));
        } else if (this.parent != null) {
            return this.driverExt.findElement(By.cssSelector("[aria-describedby*='" + parent + "'] table." + this.tableClassName),
                    Optional.of(3));

        } else {
            return this.driverExt.findElement(By.cssSelector("table." + this.tableClassName), Optional.of(3));
        }
    }

    public List<String> getListTableHeaders() {
        List<WebElement> headers = getColumnHeaders();

        List<String> headerList = new ArrayList<>();

        for (WebElement header : headers) {
            headerList.add(header.getText());
        }

        return headerList;
    }

    public void sortTableHeaderByIndex(int index, SortDirection direction) {
        List<WebElement> headers = getColumnHeaders();
        WebElement header = headers.get(index);
        
        String sortedBy = getSortedBy(index);
        
        if (sortedBy == "") {
            header.findElement(By.cssSelector("a")).click();
            waitForSorting(index, SortDirection.DESCENDING);
            sortedBy = getSortedBy(index);
        }
        
        String sortDirection = direction.getSortDirection();
        
        if (!sortDirection.equals(sortedBy)) {
            headers = getColumnHeaders();
            header = headers.get(index);
            header.findElement(By.cssSelector("a")).click();
            waitForSorting(index, direction);
        }
    }

    private String getSortedBy(int index) {
        long startTime = System.currentTimeMillis();
        String sortable = "";
        try {
            while (sortable.equals("") && (System.currentTimeMillis() - startTime) < 5000) {
                List<WebElement> headers = getColumnHeaders();

                WebElement header = headers.get(index);

                sortable = header.findElement(By.cssSelector("a")).getAttribute("class");
            }
        } catch (StaleElementReferenceException ex) {
        }

        if (sortable.toLowerCase().contains(SortDirection.ASCENDING.getSortDirection())) {
            return SortDirection.ASCENDING.getSortDirection();
        } else if (sortable.toLowerCase().contains(SortDirection.DESCENDING.getSortDirection())) {
            return SortDirection.DESCENDING.getSortDirection();
        } else {
            return "";
        }
    }
    
    private void waitForSorting(int index, SortDirection direction) {
        long startTime = System.currentTimeMillis();
        String sortedBy = "";
        String sortDirection = direction.getSortDirection();
        try {
            while((sortedBy.equals("") || !sortDirection.contains(sortedBy)) && (System.currentTimeMillis() - startTime) < 2000) {
                List<WebElement> headers = getColumnHeaders();
                
                WebElement header = headers.get(index);
                
                sortedBy = header.findElement(By.cssSelector("a")).getAttribute("class");
            }
            
        }catch (StaleElementReferenceException ex) {}
    }

    public List<String> getDataRowsTextByCellIndex(int index) {
        List<WebTableRow> rows = getDataRows();

        List<String> cellRowsData = new ArrayList<>();

        for (WebTableRow row : rows) {
            WebElement cell = row.getCellByIndex(index);
            cellRowsData.add(cell.getText());
        }

        return cellRowsData;
    }

    private void waitForSearch() {
        WebElement table = null;
        List<WebElement> rows = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        while ((rows.size() != 1) && (System.currentTimeMillis() - startTime) < 3000) {
            try {
                table = getTable();

                rows = table.findElements(By.cssSelector("tbody tr"));
            } catch (StaleElementReferenceException ex) {
            }
        }
    }

    public WebElement searchAndGetRowById(String value, String id) {
        TextEditElement search = new TextEditElement(this.driverExt, "ss", parentElement);

        search.setInputValue(value);

        WebElement table = null;
        List<WebElement> rows;
        List<WebElement> anchors;
        boolean allMatch = false;
        Optional<WebElement> row = Optional.empty();
        WebElement anchorElement = null;
        long startTime = System.currentTimeMillis();

        while ((row.isEmpty()) && (System.currentTimeMillis() - startTime) < 1000) {
            try {
                table = getTable();

                rows = table.findElements(By.cssSelector("tbody tr"));
                anchors = table.findElements(By.cssSelector("td a"));
                allMatch = anchors.stream().allMatch(x -> x.getText().contains(value));
                if (allMatch) {
                    row = rows.stream().filter(x -> x.getAttribute("data-id").contains(id)).findFirst();
                    anchorElement = row.get().findElement(By.cssSelector("td a"));
                }
            } catch (StaleElementReferenceException ex) {
            }
        }

        return anchorElement;
    }

//    private void waitForSearch(WebElement parent) {
//        WebElement table = null;
//        List<WebElement> rows = new ArrayList<>();
//        long startTime = System.currentTimeMillis();
//
//        while ((rows.size() != 1) && (System.currentTimeMillis() - startTime) < 3000) {
//            try {
//                table = parent.findElement(By.cssSelector(".compact-results-table"));
//
//                rows = table.findElements(By.cssSelector("tbody tr"));
//            } catch (StaleElementReferenceException ex) {
//            }
//        }
//    }
    
    private void waitForTableSearch(WebElement parent, String value) {
        WebElement table = null;
        Boolean found = false;
        List<WebElement> rows;
        long startTime = System.currentTimeMillis();

        while ((!found) && (System.currentTimeMillis() - startTime) < 3000) {
            try {
                table = parent.findElement(By.cssSelector(".compact-results-table"));

                rows = table.findElements(By.cssSelector("tbody tr"));
                
                if (rows.size() == 1) {
                    found = rows.stream().anyMatch(x -> x.findElement(By.cssSelector("a")).getText().contains(value));    
                }
            } catch (StaleElementReferenceException ex) {
            }
        }
    }

    public void searchTable(String value) {

        if (parentElement != null) {
            TextEditElement search = new TextEditElement(this.driverExt, "ss", parentElement);

            search.setInputValue(value);

            waitForTableSearch(parentElement, value);

        } else if (parent != null) {
            TextEditElement search = new TextEditElement(this.driverExt, "ss", parent);

            search.setInputValue(value);

            waitForSearch();

        } else {
            TextEditElement search = new TextEditElement(this.driverExt, "ss");

            search.setInputValue(value);

            waitForSearch();
        }
    }

    public void searchTable(String value, WebElement parent) {
        TextEditElement search = new TextEditElement(this.driverExt, "ss", parent);

        search.setInputValue(value);

        waitForTableSearch(parent, value);
    }

    public void clearSearch() {
        TextEditElement search = new TextEditElement(this.driverExt, "ss");
        search.clearInputValue();
    }

    private List<WebTableRow> getDataRows() {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));

        List<WebTableRow> newList = new ArrayList<>();
        for (WebElement element : rowList) {

            newList.add(new WebTableRow(this.driverExt, element));
        }

        return newList;
    }
    
    /**
     *  Waits for the table to be filtered to a single row
     */
    public void waitForFilter() {
        Integer count = 0;

        long startTime = System.currentTimeMillis();      
        
        while (count != 1 && ((System.currentTimeMillis() - startTime) < 3000)) {
            try {
                WebElement table = this.getTable();
                
                count = table.findElements(By.cssSelector("tbody tr")).size(); 
                
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }        
    }
    
    /**
     *  Waits for the table to clear filter
     */
    public void waitForClearFilter() {
        Integer count = 1;

        long startTime = System.currentTimeMillis();      
        
        while (count == 1 && ((System.currentTimeMillis() - startTime) < 2000)) {
            try {
                WebElement table = this.getTable();
                
                count = table.findElements(By.cssSelector("tbody tr")).size(); 
                
            } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ex) {
            }
        }        
    }

    public WebTableRow getDataRowByLinkName(String name) {
        waitForSearch();
        
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));
        
        WebElement element = rowList.stream().filter(x -> x.findElement(By.cssSelector("a")).getText().contains(name)).findFirst().orElseThrow();

        return new WebTableRow(this.driverExt, element);
    }

    public WebTableRow getDataRowByName(String name) {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));
        
        WebElement element = rowList.stream().filter(x -> x.findElement(By.cssSelector("td:nth-child(1)")).getText().contains(name)).findFirst().orElseThrow();

        return new WebTableRow(this.driverExt, element);
    }

    public WebTableRow getDataRowByIndex(int index) {
        List<WebElement> rowList = this.getTable().findElements(By.cssSelector("tbody tr"));

        return new WebTableRow(this.driverExt, rowList.get(index));
    }

    private List<WebElement> getColumnHeaders() {
        return this.getTable().findElements(By.cssSelector("tr th"));
    }

    public String getTableMessage() {
        return this.driverExt.findElement(By.cssSelector(".empty-list"), Optional.of(2)).getText();
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