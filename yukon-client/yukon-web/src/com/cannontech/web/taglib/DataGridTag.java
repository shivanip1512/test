package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class DataGridTag extends YukonTagSupport {
    
    private class SortableGridElement{
        
        private String key;
        private String content;
        
        public SortableGridElement(String key, String content){
            this.key = key;
            this.content = content;
        }

        public String getKey() {
            return key;
        }

        public String getContent() {
            return content;
        }
    }
    
    private class ElementComparator implements Comparator<SortableGridElement>{

        @Override
        public int compare(SortableGridElement e1, SortableGridElement e2) {
            return e1.getKey().toLowerCase().compareTo(e2.getKey().toLowerCase());
        }
        
    }
    
    private LinkedList<SortableGridElement> elements = Lists.newLinkedList();
    private SortableGridElement[][] grid;
    private int numberOfColumns;
    private int numberOfRows;
    private String tableClasses = "";
    private String tableStyle = "";
    private String rowStyle = "";
    private String cellStyle = "";
    private String orderMode = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        
        getJspBody().invoke(null);
        
        //Calculate the required number of rows
        numberOfRows = elements.size() / numberOfColumns;
        if(elements.size() % numberOfColumns!=0){
            numberOfRows++;
        }
        
        //Create the data grid
        grid = new SortableGridElement[numberOfRows][numberOfColumns];
        
        
        /* Sort the elements based on their sort key which is required 
         * for left to right ordering and also top to bottom
         */
        if(orderMode.toLowerCase().equals("lefttoright") || 
                orderMode.toLowerCase().equals("toptobottom")){
            Collections.sort(elements, new ElementComparator());
        }
        
        //Populate the grid
        for(int i = 0; i < elements.size();i++){
            grid[ i / numberOfColumns][i % numberOfColumns] = elements.get(i);
            
        }
        
        //Rearrange grid elements for top to bottom mode
        if(orderMode.toLowerCase().equals("toptobottom")){
            arrangeTopToBottom();
        }
        
        getJspContext().getOut().println("<table ");
        if(StringUtils.isNotBlank(tableClasses)) {
            getJspContext().getOut().println("class=\"" + tableClasses + "\" ");
        }
        
        getJspContext().getOut().println("style=\"border-collapse:collapse;" + tableStyle + "\">");
        
        for(int i = 0; i < numberOfRows;i++){
            for(int j = 0; j < numberOfColumns; j++){

                boolean firstCell = j == 0;
                boolean lastCell = j == numberOfColumns - 1; 

                if(firstCell){
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
                if(StringUtils.isNotBlank(cellStyle)) {
                    getJspContext().getOut().println("style=\"" + cellStyle + "\"");
                }

                /* Do contents */
                getJspContext().getOut().print(">");
               
                //Some Elements in the grid might be null, leave table cell empty for null values
                if(grid[i][j]!=null){
                    getJspContext().getOut().print(grid[i][j].getContent());
                }
                getJspContext().getOut().println("</td>");
            }
            getJspContext().getOut().println("</tr>");
        }
        getJspContext().getOut().println("</table>");
    }    
    
    // Changes the arrangement of grid elements for top to bottom ordering
    private void arrangeTopToBottom(){
        
        SortableGridElement[][] newGrid = new SortableGridElement[numberOfRows][numberOfColumns];
        int destRow = 0; int destColumn = 0;
        for(int i = 0; i< numberOfRows; i++){
            for(int j = 0; j<numberOfColumns;j++){
                
                newGrid[destRow][destColumn] = grid[i][j];
                
                destRow++;
                if(destRow==numberOfRows){
                    destRow = 0;
                    destColumn++;
                }
            }
        }
        
        grid = newGrid;
    }

    public void addContent(String key, String content) {
        elements.add(new SortableGridElement(key, content));
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
        return orderMode;
    }

    public void setOrderMode(String ordering) {
        this.orderMode = ordering;
    }
}