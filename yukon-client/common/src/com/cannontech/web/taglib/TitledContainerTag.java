package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Generates HTML to wrap the body in a nice rounded box with a
 * shaded area at the top for the title.
 */
public class TitledContainerTag extends TagSupport {
    private String title;
    
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print("  <table class=\"roundedTable\">\n" + 
                    "    <tr> \n" + 
                    "      <td class=\"upperLeft\"></td>\n" + 
                    "      <td class=\"top\">");
            
            pageContext.getOut().print(getTitle());
            
            pageContext.getOut().print("</td>\n" + 
                    "      <td class=\"upperRight\"></td>\n" + 
                    "    </tr>\n" + 
                    "    <tr>\n" + 
                    "      <td class=\"leftSide\"></td>\n" + 
                    "      <td>\n\n");
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print("\n\n      </td>\n" + 
                    "      <td class=\"rightSide\"></td>\n" + 
                    "    </tr>\n" + 
                    "    <tr>\n" + 
                    "      <td class=\"lowerLeft\"></td>\n" + 
                    "      <td class=\"bottom\"></td>\n" + 
                    "      <td class=\"lowerRight\"></td>\n" + 
                    "    </tr>\n" + 
                    "  </table>\n" + 
                    "");
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
}
