package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Generates HTML to wrap the body in a nice rounded box with a shaded area at
 * the top for the title.
 */
public class TitledContainerTag extends TagSupport {
    private String title;
    private String styleClass = "";
    private String id = null;

    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print("<div class=\"titledContainer " + getStyleClass() + "\" "
                    + ((id != null) ? "id=\"" + id + "\"" : "") + ">\n"
                    + "           <span class=\"rtop\">\n"
                    + "             <span class=\"lt1\"></span>\n"
                    + "             <span class=\"lt2\"></span>\n"
                    + "             <span class=\"lt3\"></span>\n"
                    + "             <span class=\"lt4\"></span>\n"
                    + "             <span class=\"lt5\"></span>\n"
                    + "             <span class=\"lt6\"></span>\n" + "           </span>\n"
                    + "           <div class=\"title\">" + getTitle() + "</div>\n"
                    + "           <div class=\"content\">\n");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print("</div>" + "           <span class=\"lbottom\">"
                    + "             <span class=\"l6\"></span>"
                    + "             <span class=\"l5\"></span>"
                    + "             <span class=\"l4\"></span>"
                    + "             <span class=\"l3\"></span>"
                    + "             <span class=\"l2\"></span>"
                    + "             <span class=\"l1\"></span>" + "           </span>"
                    + "           <span class=\"rtbottom\">"
                    + "             <span class=\"r6\"></span>"
                    + "             <span class=\"r5\"></span>"
                    + "             <span class=\"r4\"></span>"
                    + "             <span class=\"r3\"></span>"
                    + "             <span class=\"r2\"></span>" + "           </span>"
                    + "           <span class=\"rbottom\">"
                    + "             <span class=\"r1\"></span>" + "           </span>"
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
