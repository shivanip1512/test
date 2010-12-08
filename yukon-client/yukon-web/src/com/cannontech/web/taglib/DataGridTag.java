package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class DataGridTag extends YukonTagSupport {
    
    private List<String> content = Lists.newArrayList();
    private int numberOfColumns;
    private String tableClasses = "";
    private String tableStyle = "";
    private String rowStyle = "";
    private String cellStyle = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        
        getJspBody().invoke(null);
        getJspContext().getOut().println("<table ");
        
        if(StringUtils.isNotBlank(tableClasses)) {
            getJspContext().getOut().println("class=\"" + tableClasses + "\" ");
        }
        
        getJspContext().getOut().println("style=\"border-collapse:collapse;" + tableStyle + "\">");
        
        int i = 0;
        for (String item : content) {
            /* Do fancy % math to figure out if beginning or end of row is upon us */
            boolean firstCell = i % numberOfColumns == 0;
            boolean lastCell = i % numberOfColumns == numberOfColumns - 1; 
            
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
            if(StringUtils.isNotBlank(cellStyle)) {
                getJspContext().getOut().println("style=\"" + cellStyle + "\"");
            }
            
            /* Do contents */
            getJspContext().getOut().println(">" + item + "</td>");
            
            /* End row */
            if (lastCell) {
                getJspContext().getOut().println("</tr>");
            }
            i++;
        }
        
        int numberOfColumnsInLastRow = i % numberOfColumns;
        if (numberOfColumnsInLastRow > 0) {
            int numberOfBlankColumns = numberOfColumns - numberOfColumnsInLastRow;
            /* Generate some <td></td> */
            for(int j = 0; j < numberOfBlankColumns; j++) {
                getJspContext().getOut().println("<td></td>");
            }
            
            getJspContext().getOut().println("</tr>");
        }
        
        getJspContext().getOut().println("</table>");
    }

    public void addContent(String string) {
        content.add(string);
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

}