package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Generates HTML to wrap the body in a nice rounded box with a shaded area at
 * the top for the title.
 * 
 * Note: this code is duplicated in roundedContainer.tag
 * 
 */
public class TitledContainerTag extends TagSupport {
    private String title;
    private String styleClass = "";
    private String id = null;

    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print("<div class=\"titledContainer roundedContainer " + getStyleClass() + "\" "
                    + ((id != null) ? "id=\"" + id + "\"" : "") + ">\n"
                    + "           <span class=\"top\">\n"
                    + "             <span class=\"t1\"></span>\n"
                    + "             <span class=\"t2\"></span>\n"
                    + "             <span class=\"t3\"></span>\n"
                    + "             <span class=\"t4\"></span>\n"
                    + "             <span class=\"t5\"></span>\n"
                    + "             <span class=\"t6\"></span>\n" 
                    + "           </span>\n                      "
                    + "           <div class=\"titleBar roundedContainer_titleBar\">"
                    + "           <div class=\"title roundedContainer_title\">" + getTitle() + "</div>"
                    + "           </div>\n"
                    + "           <div class=\"content roundedContainer_content\">\n");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print("<br class=\"makesNoSense\"></div>                " 
                    + "           <div class=\"lbottom\">    "
                    + "             <span class=\"l5\"></span>"
                    + "             <span class=\"l4\"></span>"
                    + "             <span class=\"l3\"></span>"
                    + "             <span class=\"l2\"></span>"
                    + "             <span class=\"l1\"></span>"
                    + "           </div>                     "
                    + "           <div class=\"rbottom\">    "
                    + "             <span class=\"r5\"></span>"
                    + "             <span class=\"r4\"></span>"
                    + "             <span class=\"r3\"></span>"
                    + "             <span class=\"r2\"></span>"
                    + "             <span class=\"r1\"></span>" 
                    + "           </div>                     "
                    + "           <div class=\"bottomBar\"></div>"
                    + "       </div>");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

}
