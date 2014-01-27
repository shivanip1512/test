package com.cannontech.web.taglib.nav;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.cannontech.web.taglib.StandardPageTag;

/**
 * Writes out the given URLs in a bread crumb format.
 *
 * @author: ryan
 */
public class BreadCrumbsTag extends BodyTagSupport {
    
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }
    
    public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
    }
    
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        try {
            // if we are within the standardPage tag, send it our content, otherwise just output
            StandardPageTag standardPageTag = (StandardPageTag) findAncestorWithClass(this, StandardPageTag.class);
            if (standardPageTag != null) {
                standardPageTag.setBreadCrumbs(getWholeTag());
            } else {
                pageContext.getOut().write(getWholeTag());
            }
        } catch (IOException e) {
            throw new JspException("Unable to output bread crumbs.", e);
        }
        return EVAL_PAGE;
    }
    
    private String getWholeTag() {
        StringBuffer buf = new StringBuffer();
        buf.append("<ol class=\"breadcrumb\">");
        buf.append(bodyContent.getString());
        buf.append("</ol>");
        buf.append("\n");
        return buf.toString();
    }

}