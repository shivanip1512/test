package com.cannontech.web.util;

import com.cannontech.web.exceptions.NullRowException;

public class TableRowFormater {

    private static TableRow tableRow;
  
    public TableRowFormater(){
        super();
    }
    
    public TableRowFormater(TableRow row) throws NullRowException {
        if (!isRowNull (row)) {
            tableRow = row;
        }
        else {
            throw new NullRowException("Row passed containts null objects");
        }
   }
    
    public TableRow formatRow() throws NullRowException {
        if (!isRowNull (tableRow)) {
            for (int i=0; i < tableRow.getColumnNumber(); i++) {
                String newCell = formatCellContentForColumnName(tableRow.getCell(i), tableRow.getHeaderColumn(i));
                tableRow.setCell(i, newCell);
            }
        }
        else {
            throw new NullRowException("Row passed containts null objects");
        }
            
        return tableRow;
    }
    
    public  String formatCellContentForColumnName(String cellContent, String columnName) {
        if (cellContent.length() < columnName.length()){
            for (int i = 0; i < (columnName.length() - cellContent.length()); i++) {
                cellContent += "&nbsp;";
            }
            cellContent += "&nbsp;&nbsp;&nbsp;";
        }
        return cellContent;
    }
    
    private boolean isRowNull(TableRow row) {
        if (row == null){
            return true;
        }
        if (row != null) {
            for (int i = 0; i < row.getColumnNumber(); i++){
                if (row.getCell(i) == null) {
                    return true;
                }
             }
        }
        return false;
    }

    public static TableRow getTableRow() {
        return tableRow;
    }

    public  void setTableRow(TableRow tableRow) throws NullRowException {
        if (!isRowNull(tableRow)){
            TableRowFormater.tableRow = tableRow;
        }
        else {
            throw new NullRowException("Row passed containts null objects");
        }
        
    }
    


}
