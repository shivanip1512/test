package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.Writable;

public class OutputContentTag extends SimpleTagSupport {
    
    private Writable writable;

    @Override
    public void doTag() throws JspException {
        if (writable != null) {
            try {
                writable.write(getJspContext().getOut());
            } catch (IOException e) {
                throw new JspException("Unable to output writable", e);
            }
        }
    }
    
    public void setWritable(Writable writable) {
        this.writable = writable;
    }

}
