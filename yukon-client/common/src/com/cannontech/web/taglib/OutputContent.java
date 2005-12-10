package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * This tag will output the content captured by the StandardPageTag. It should only
 * be used in a page template/wrapper/layout, not in a file that is directly accessed.
 */
public class OutputContent extends TagSupport {

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        
        try {
            BodyContent bodyContent = (BodyContent) 
              pageContext.getAttribute(StandardPageTag.CTI_MAIN_CONTENT, PageContext.REQUEST_SCOPE);
            bodyContent.writeOut(pageContext.getOut());
        } catch (IOException e) {
            throw new JspException("Can't output body content.", e);
        }
        return EVAL_PAGE;
    }

}
