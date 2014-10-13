package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cannontech.web.CssLibrary;

public class IncludeCssTag extends SimpleTagSupport {
    
    private String link;
    private boolean force = false; // force a <link> tag to be written to the output
    
    public void doTag() throws JspException {
        
        StandardPageTag spTag = StandardPageTag.find(getJspContext());
        
        if (this.force) {
            // Beware of multiple includes!
            JspWriter out = getJspContext().getOut();
            try {
                PageContext pageContext = (PageContext) getJspContext();
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                out.write("<link rel=\"stylesheet\" href=\"");
                out.write(request.getContextPath() + resolveLink());
                out.write("\">");
            } catch (IOException e) {
                // This should not happen unless the sky is falling.
                throw new RuntimeException(e);
            }
        } else if (spTag != null) {
            spTag.addCSSFile(resolveLink());
        }
        
        return;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public void setForce(boolean force) {
        this.force = force;
    }
    
    private String resolveLink() {
        try {
            return CssLibrary.valueOf(link).getPath();
        } catch (IllegalArgumentException e) {
            return link;
        }
    }
    
}