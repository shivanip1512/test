package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class DataGridTag extends YukonTagSupport {

    public enum OrderMode {
        NONE(""),
        TOP_TO_BOTTOM("topToBottom"),
        LEFT_TO_RIGHT("leftToRight");

        private final String strValue;

        private OrderMode(String strValue) {
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }
    }

    private LinkedList<String> content = Lists.newLinkedList();
    private String[][] grid;
    private int numberOfColumns;
    private int numberOfRows;
    private String tableClasses = "";
    private String tableStyle = "";
    private String rowStyle = "";
    private String cellStyle = "";
    private OrderMode orderMode = OrderMode.NONE;

    @Override
    public void doTag() throws JspException, IOException {

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
            } else { // orderMode is either NONE or LEFT_TO_RIGHT
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
            for (int j = 0; j < numberOfColumns; j++) {

                boolean firstCell = j == 0;
                boolean lastCell = j == numberOfColumns - 1;

                if (firstCell) {
                    getJspContext().getOut().println("<tr");
                    if (StringUtils.isNotBlank(rowStyle)) {
                        getJspContext().getOut().println(" style=\"" + rowStyle + "\"");
                    }
                    getJspContext().getOut().println(">");
                }

                getJspContext().getOut().println("<td class=\"");

                /* Add first,last,middle target class */
                if (firstCell) {
                    getJspContext().getOut().println("first");
                } else if (lastCell) {
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
        if (orderMode.toLowerCase().equals("toptobottom")) {
            this.orderMode = OrderMode.TOP_TO_BOTTOM;
        } else if (orderMode.toLowerCase().equals("lefttoright")) {
            this.orderMode = OrderMode.LEFT_TO_RIGHT;
        } else {
            this.orderMode = OrderMode.NONE;
        }
    }
}