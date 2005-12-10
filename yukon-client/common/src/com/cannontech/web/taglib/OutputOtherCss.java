package com.cannontech.web.taglib;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Outputs a <link> for CSS for any CSS file specified with the IncludeCssTag
 * within the StandardPageTag.
 */
public class OutputOtherCss extends TagSupport {

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }
    public int doEndTag() throws JspException {
        try {
            List cssList = (List) pageContext.getAttribute(StandardPageTag.CTI_CSS_FILES,
                                                           PageContext.REQUEST_SCOPE);
            for (Iterator iter = cssList.iterator(); iter.hasNext();) {
                String cssFile = (String) iter.next();
                pageContext.getOut()
                           .write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
                pageContext.getOut().write(cssFile);
                pageContext.getOut().write("\" >");
            }
            return EVAL_PAGE;
        } catch (Exception e) {
            throw new JspException("Can't output other CSS files.", e);
        }
    }

}
