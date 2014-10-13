package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.JsLibrary;


public class IncludeScriptTag extends SimpleTagSupport {
    
    private String link;
    private boolean force = false; // force a <script> tag to be written to the output

    @Override
    public void doTag() throws JspException {
        
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        if (this.force) {
            // Beware of multiple includes!
            JspWriter out = getJspContext().getOut();
            try {
                PageContext pageContext = (PageContext) getJspContext();
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(request.getContextPath() + resolveLink());
                out.write("\"></script>");
            } catch (IOException e) {
                // This should not happen unless the sky is falling.
                throw new RuntimeException(e);
            }
        } else if (spTag != null) {
            spTag.addScriptFile(resolveLink());
        }
        
        return;
    }
    
    private String resolveLink() {
        try {
            return JsLibrary.valueOf(link).getPath();
        } catch (IllegalArgumentException e) {
            return link;
        }
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
}