package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class DataGridTag extends YukonTagSupport {

    public enum OrderMode {
        TOP_TO_BOTTOM, LEFT_TO_RIGHT;
    }
    
    private LinkedList<String> content = Lists.newLinkedList();
    private int numberOfColumns;
    private String tableClasses = "";
    private String tableStyle = "";
    private String rowStyle = "";
    private String cellStyle = "";
    private OrderMode orderMode = OrderMode.LEFT_TO_RIGHT;

    @Override
    public void doTag() throws JspException, IOException {
        int numberOfRows;
        String[][] grid;
        
        getJspBody().invoke(null);

        // Calculate the required number of rows
        numberOfRows = content.size() / numberOfColumns;
        if (content.size() % numberOfColumns != 0) {
            numberOfRows++;
        }

        // Create the data grid
        grid = new String[numberOfRows][numberOfColumns];

        // Populate the grid based on orderMode
        int index = 0;
        for (String element : content) {
            if (orderMode == OrderMode.TOP_TO_BOTTOM) {
                grid[index % numberOfRows][index / numberOfRows] = element;
            } else { // orderMode is LEFT_TO_RIGHT
                grid[index / numberOfColumns][index % numberOfColumns] = element;
            }
            index++;
        }

        getJspContext().getOut().println("<table ");
        if (StringUtils.isNotBlank(tableClasses)) {
            getJspContext().getOut().println("class=\"" + tableClasses + "\" ");
        }

        getJspContext().getOut().println("style=\"border-collapse:collapse;" + tableStyle + "\">");

        for (int i = 0; i < numberOfRows; i++) {
            
            //Start new row
            getJspContext().getOut().println("<tr");
            if (StringUtils.isNotBlank(rowStyle)) {
                getJspContext().getOut().println(" style=\"" + rowStyle + "\"");
            }
            getJspContext().getOut().println(">");
            
            for (int j = 0; j < numberOfColumns; j++) {
                getJspContext().getOut().println("<td class=\"");

                /* Add first,last,middle target class */
                if (j==0) { 
                    getJspContext().getOut().println("first");
                } else if (j==numberOfColumns-1) {
                    getJspContext().getOut().println("last");
                } else {
                    getJspContext().getOut().println("middle");
                }
                getJspContext().getOut().println("\"");

                /* Add style */
                if (StringUtils.isNotBlank(cellStyle)) {
                    getJspContext().getOut().println("style=\"" + cellStyle + "\"");
                }

                /* Do contents */
                getJspContext().getOut().print(">");

                // Some Elements in the grid might be null, leave table cell empty for null values
                if (grid[i][j] != null) {
                    getJspContext().getOut().print(grid[i][j]);
                }
                getJspContext().getOut().println("</td>");
            }
            getJspContext().getOut().println("</tr>");
        }
        getJspContext().getOut().println("</table>");
    }

    public void addContent(String content) {
        this.content.add(content);
    }

    public void setCols(int cols) {
        this.numberOfColumns = cols;
    }

    public String getTableClasses() {
        return tableClasses;
    }

    public void setTableClasses(String tableClasses) {
        this.tableClasses = tableClasses;
    }

    public String getTableStyle() {
        return tableStyle;
    }

    public void setTableStyle(String tableStyle) {
        this.tableStyle = tableStyle;
    }

    public String getCellStyle() {
        return cellStyle;
    }

    public void setCellStyle(String cellStyle) {
        this.cellStyle = cellStyle;
    }

    public String getRowStyle() {
        return rowStyle;
    }

    public void setRowStyle(String rowStyle) {
        this.rowStyle = rowStyle;
    }

    public String getOrderMode() {
        return orderMode.toString();
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = OrderMode.valueOf(orderMode);
    }
}