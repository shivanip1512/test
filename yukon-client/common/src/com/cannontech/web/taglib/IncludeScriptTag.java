package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.taglib.JSLibrary;

public class IncludeScriptTag extends SimpleTagSupport {
    private String link;
    private Boolean force = false;
    private String component = "";

    public void doTag() throws JspException {
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (spTag != null) {
            spTag.addScriptFile(getLink());
        } else {
            //Beware of multiple includes!
            JspWriter out = getJspContext().getOut();
            try {
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(getLink());
                out.write("\"></script>");
            } catch (IOException e) {
            }
        }
        
        return;
    }
    
    public String getComponent() {
        return component;
    }
    
    public void setComponent(String component) {
        this.component = component;
    }
    
    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }
    
    public String getLink() {
        // lookup the enum
        if(force) {
            if(component.isEmpty()) {
                return JSLibrary.valueOf(link).getDefaultInclude();
            } else {
                return JSLibrary.valueOf(link).getPath() + component;
            }
        } else {
            return link;
        }
    }

    public void setLink(String link) {
        this.link = link;
    }
}
