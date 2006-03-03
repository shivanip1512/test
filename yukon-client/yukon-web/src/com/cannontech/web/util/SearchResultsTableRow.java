package com.cannontech.web.util;

public class SearchResultsTableRow implements TableRow {
    
    private static final String [] COLUMN_NAMES = {"Name", "Item Type", "Description"};
    private String[] cells;
    
    public SearchResultsTableRow() {
        super();
        cells = new String[getColumnNumber()];
    }
    

    public SearchResultsTableRow(String[] cells) {
        super();
        this.cells = cells;
    }


    public int getColumnNumber() {
        return COLUMN_NAMES.length;         
    }

    public String getCell(int i) {
        return cells[i];
    }

    public String getHeaderColumn(int i) {
        
        return COLUMN_NAMES[i];
    }

    public void setCell(int i, String newCell) {
        cells[i] = newCell;
    }

}
