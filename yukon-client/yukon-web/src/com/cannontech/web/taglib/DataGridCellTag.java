package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;

public class DataGridCellTag extends YukonTagSupport {
    
    private String sortKey = "";
    
    @Override
    public void doTag() throws JspException, IOException {
        StringWriter stringWriter = new StringWriter();
        getJspBody().invoke(stringWriter);
        
        String contentString = stringWriter.toString();
        
        DataGridTag parent = getParent(DataGridTag.class);
        if(!parent.getOrderMode().equals("") && sortKey.equals("")){
            sortKey = contentString;
        }
        parent.addContent(sortKey, contentString);
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
