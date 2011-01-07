package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.taglib.JsLibrary;

public class IncludeScriptTag extends SimpleTagSupport {
    private String link;
    private boolean force = false;          //force a <script> tag to be written to the output

    public void doTag() throws JspException {
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (spTag != null) {
            spTag.addScriptFile(resolveLink());
        } else if(this.force) {
            //Beware of multiple includes!
            JspWriter out = getJspContext().getOut();
            try {
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(resolveLink());
                out.write("\"></script>");
            } catch (IOException e) {
            }
        }
        
        return;
    }
    
    public String resolveLink() {
        try {
            return JsLibrary.valueOf(this.link).getPath();
        } catch(IllegalArgumentException e) {
            return this.link;
        }
    }
    
    public boolean getForce() {
        return this.force;
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
