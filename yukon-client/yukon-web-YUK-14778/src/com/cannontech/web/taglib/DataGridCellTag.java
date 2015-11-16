package com.cannontech.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;

public class DataGridCellTag extends YukonTagSupport {
    
    @Override
    public void doTag() throws JspException, IOException {
        StringWriter stringWriter = new StringWriter();
        getJspBody().invoke(stringWriter);
        
        DataGridTag parent = getParent(DataGridTag.class);
        parent.addContent(stringWriter.toString());
    }
}
