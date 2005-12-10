package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This tag will output the strictest doctype possible taking into account the
 * doctype of the content page and the wrapper page. See the htmlLevel attribute to the
 * StandardPageTag to set the level of the content page and this tag's htmlLevel 
 * attribute to set the level of the wrapper page.
 */
public class OutputDoctypeTag extends TagSupport {
    private static final String HTML_TRANSITIONAL_DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
    private static final String HTML_STRICT_DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
    
    private String htmlLevel = StandardPageTag.HTML_TRANSITIONAL;
    
    public void release() {
        htmlLevel = StandardPageTag.HTML_TRANSITIONAL;
        super.release();
    }
    
    public int doEndTag() throws JspException {
        try {
            if (getHtmlLevel().equals(StandardPageTag.HTML_TRANSITIONAL)) {
                // the standard wrapper is no strict, do it doesn't matter what the content is
                pageContext.getOut().write(HTML_TRANSITIONAL_DOCTYPE);
            } else if (getContentDoctype().equals(StandardPageTag.HTML_TRANSITIONAL)) {
                // the wrapper is strict, but the content is transitional
                pageContext.getOut().write(HTML_TRANSITIONAL_DOCTYPE);
            } else {
                // the wrapper and the content are both strict
                pageContext.getOut().write(HTML_STRICT_DOCTYPE);
            }
        } catch (IOException e) {
            throw new JspException("Cannot output doctype", e);
        }
        return EVAL_PAGE; 
    }
    
    private String getContentDoctype() {
        return (String) pageContext.getAttribute(StandardPageTag.CTI_DOCTYPE_LEVEL, PageContext.REQUEST_SCOPE);
    }

    public String getHtmlLevel() {
        return htmlLevel;
    }

    public void setHtmlLevel(String htmlLevel) {
        this.htmlLevel = htmlLevel;
    }
}
