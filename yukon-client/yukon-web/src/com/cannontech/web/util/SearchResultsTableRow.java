package com.cannontech.web.util;

import com.cannontech.web.exceptions.NullRowException;

public class SearchResultsTableRow implements TableRow {
    
      private static class Column {
          private String name;
          private int pos;
          
          private  Column() {
            super();
        }
         private  Column(String n, int p) {
            name = n;
            pos = p;
        }
        public String getName() {
            return name;
        }
        public int getPos() {
            return pos;
        }        
    }
    
    private final  static Column[] COLUMNS = {new Column  ("Name", 0), 
                                              new Column  ("Item Type",1), 
                                              new Column  ("Description", 2),
                                              new Column  ("Parent", 3)};
    private String[] cells;
    private static TableRowFormater formater;
    
    private SearchResultsTableRow() {
        super();
        cells = new String[getColumnNumber()];
    }
    

    public SearchResultsTableRow(String[] cells) {
        super();
        this.cells = cells;
    }
    
    public int getColumnNumber() {
        if (cells == null)
            return 0;
        return cells.length;         
    }
    
    public String getCell(int i) {
        return cells[i];
    }

    public static String[] getColumnNames() {
        String[] names = new String[COLUMNS.length];
        for (int i = 0; i < COLUMNS.length; i++) {
            names[i] = ((Column)COLUMNS[i]).getName();            
        }
        return names;
    }
    
    public String getHeaderColumn(int i) {        
        return ((Column)COLUMNS[i]).getName();
    }

    public void setCell(int i, String newCell) {
        cells[i] = newCell;
    }
    
    public void format() throws NullRowException {
        getFormater().setTableRow(this);        
        getFormater().formatRow();       
    }
    
    public static TableRowFormater getFormater() {
        if (formater == null)
            formater = new TableRowFormater();
        return formater;
    }

}
