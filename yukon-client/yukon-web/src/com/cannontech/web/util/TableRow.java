package com.cannontech.web.util;

public interface TableRow {

    int getColumnNumber();

    String getHeaderColumn(int i);

    String getCell(int i);

    void setCell(int i, String newCell);

}
