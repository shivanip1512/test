package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import com.google.common.collect.Lists;

public class DataGridTag extends YukonTagSupport {
    
    private List<String> content = Lists.newArrayList();
    private int numberOfColumns;
    private String tableClasses = "";
    private String tableStyle = "";
    private String tableAlign = "";
    private String rowStyle = "";
    private String cellStyle = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        
        getJspBody().invoke(null); // possible that null is the best choice
        getJspContext().getOut().println("<table align=\"" + tableAlign + "\" class=\"" + tableClasses + "\" style=\"" + tableStyle + "\">");
        
        int i = 0;
        for (String item : content) {
            // do fancy % math to figure out if beginning or end of row is upon us
            if (i % numberOfColumns == 0) {
                getJspContext().getOut().println("<tr style=\"" + rowStyle + "\">");
            }
            
            getJspContext().getOut().println("<td style=\"" + cellStyle + "\">" + item + "</td>");
            
            if (i % numberOfColumns == numberOfColumns - 1) {
                getJspContext().getOut().println("</tr>");
            }
            i++;
        }
        
        int numberOfColumnsInLastRow = i % numberOfColumns;
        if (numberOfColumnsInLastRow > 0) {
            int numberOfBlankColumns = numberOfColumns - numberOfColumnsInLastRow;
            // generate some <td></td>
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

    public String getTableAlign() {
        return tableAlign;
    }

    public void setTableAlign(String tableAlign) {
        this.tableAlign = tableAlign;
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
